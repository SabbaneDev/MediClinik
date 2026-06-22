package controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.Appointment;
import models.AppointmentDAO;
import models.PatientDAO;

// Gère le tableau de bord principal
public class MainController {

    @FXML private StackPane contentArea;
    @FXML private VBox      dashboardPane;
    @FXML private Button    btnDashboard;
    @FXML private Button    btnPatients;
    @FXML private Button    btnAppointments;
    @FXML private Label     lblTotalPatients;
    @FXML private Label     lblTotalAppointments;
    @FXML private Label     lblUpcomingAppointments;

    @FXML private TableView<Appointment> tableDashAppointments;
    @FXML private TableColumn<Appointment, LocalDateTime> colDashDate;
    @FXML private TableColumn<Appointment, String> colDashPatient;
    @FXML private TableColumn<Appointment, String> colDashDoctor;
    @FXML private TableColumn<Appointment, LocalDateTime> colDashStatus;

    @FXML private Button btnQuickAddPatient;
    @FXML private Button btnQuickBookAppointment;
    @FXML private ImageView imgLogo;

    private final PatientDAO     patientDAO     = new PatientDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final ObservableList<Appointment> dashAppointmentList = FXCollections.observableArrayList();

    // Initialise la vue du tableau de bord
    @FXML
    public void initialize() {
        colDashDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        colDashPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colDashDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colDashStatus.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));

        colDashDate.setCellFactory(column -> new TableCell<Appointment, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        colDashStatus.setCellFactory(column -> new TableCell<Appointment, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    LocalDate today = LocalDate.now();
                    LocalDate apptDate = item.toLocalDate();
                    
                    Label badge = new Label();
                    badge.getStyleClass().clear();
                    if (apptDate.isEqual(today)) {
                        badge.setText("Aujourd'hui");
                        badge.getStyleClass().add("badge-today");
                    } else {
                        badge.setText("À venir");
                        badge.getStyleClass().add("badge-upcoming");
                    }
                    setGraphic(badge);
                }
            }
        });

        tableDashAppointments.setOnScroll(e -> e.consume());

        refreshDashboardStats();

        try {
            String logoUrl = System.getProperty("app.logo.url");
            Image logo = null;
            if (logoUrl != null && !logoUrl.trim().isEmpty()) {
                logo = new Image(logoUrl, true);
            } else {
                java.io.InputStream is = getClass().getResourceAsStream("/images/clinic.png");
                if (is != null) logo = new Image(is);
            }
            if (logo != null && imgLogo != null) {
                imgLogo.setImage(logo);
            }
        } catch (Exception e) {
            System.err.println("Failed to load sidebar logo: " + e.getMessage());
        }
    }

    // Actualise les statistiques du tableau de bord
    public void refreshDashboardStats() {
        lblTotalPatients.setText(String.valueOf(patientDAO.getPatientCount()));
        lblTotalAppointments.setText(String.valueOf(appointmentDAO.getAppointmentCount()));
        lblUpcomingAppointments.setText(String.valueOf(appointmentDAO.getUpcomingCount()));

        dashAppointmentList.clear();
        List<Appointment> recentAppts = appointmentDAO.getRecentUpcomingAppointments(5);
        dashAppointmentList.addAll(recentAppts);
        tableDashAppointments.setItems(dashAppointmentList);
    }

    // Affiche la vue du tableau de bord
    @FXML
    void showDashboardView(ActionEvent event) {
        contentArea.getChildren().setAll(dashboardPane);
        refreshDashboardStats();
        setActive(btnDashboard);
    }

    // Affiche la vue des patients
    @FXML
    void showPatientsView(ActionEvent event) {
        loadView("/views/patient-view.fxml");
        setActive(btnPatients);
    }

    // Affiche la vue des rendez-vous
    @FXML
    void showAppointmentsView(ActionEvent event) {
        loadView("/views/appointment-view.fxml");
        setActive(btnAppointments);
    }

    // Ajoute un patient
    @FXML
    void handleQuickAddPatient(ActionEvent event) {
        loadViewAndFocus("/views/patient-view.fxml", "#txtFirstName");
        setActive(btnPatients);
    }

    // Ajoute un rendez-vous
    @FXML
    void handleQuickBookAppointment(ActionEvent event) {
        loadViewAndFocus("/views/appointment-view.fxml", "#cmbPatient");
        setActive(btnAppointments);
    }

    private void loadView(String fxmlPath) {
        loadViewAndFocus(fxmlPath, null);
    }

    private void loadViewAndFocus(String fxmlPath, String focusSelector) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(view);
            if (focusSelector != null) {
                javafx.application.Platform.runLater(() -> {
                    javafx.scene.Node node = view.lookup(focusSelector);
                    if (node != null) {
                        node.requestFocus();
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Cannot load view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void setActive(Button active) {
        for (Button b : new Button[]{btnDashboard, btnPatients, btnAppointments}) {
            b.getStyleClass().remove("sidebar-nav-active");
        }
        if (!active.getStyleClass().contains("sidebar-nav-active")) {
            active.getStyleClass().add("sidebar-nav-active");
        }
    }

    // Fermer la fenêtre de l'application
    @FXML
    void handleCloseWindow(ActionEvent event) {
        btnDashboard.getScene().getWindow().hide();
    }

    // Réduire la fenêtre
    @FXML
    void handleMinimizeWindow(ActionEvent event) {
        ((javafx.stage.Stage) btnDashboard.getScene().getWindow()).setIconified(true);
    }

    // Agrandit ou réduit la fenêtre
    @FXML
    void handleMaximizeWindow(ActionEvent event) {
        javafx.stage.Stage s = (javafx.stage.Stage) btnDashboard.getScene().getWindow();
        s.setMaximized(!s.isMaximized());
    }
}
