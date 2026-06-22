package controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.Appointment;
import models.AppointmentDAO;
import models.Patient;
import models.PatientDAO;

// Gère les rendez-vous de la clinique
public class AppointmentController {

    @FXML
    private ComboBox<Patient> cmbPatient;

    @FXML
    private TextField txtDoctorName;

    @FXML
    private DatePicker dpDate;

    @FXML
    private ComboBox<String> cmbHour;

    @FXML
    private ComboBox<String> cmbMinute;

    @FXML
    private TextArea txtDescription;

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnDetails;

    @FXML
    private TableView<Appointment> tableAppointments;

    @FXML
    private TableColumn<Appointment, Integer> colId;

    @FXML
    private TableColumn<Appointment, LocalDateTime> colDate;

    @FXML
    private TableColumn<Appointment, String> colPatientName;

    @FXML
    private TableColumn<Appointment, String> colDoctorName;

    @FXML
    private TableColumn<Appointment, String> colDescription;

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PatientDAO patientDAO = new PatientDAO();

    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private final ObservableList<Patient> patientSelectorList = FXCollections.observableArrayList();

    private Appointment selectedAppointment = null;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Initialise la vue des rendez-vous
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colDate.setCellFactory(column -> new TableCell<Appointment, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DATE_TIME_FORMATTER));
                }
            }
        });

        loadPatientDropdown();
        refreshAppointmentTable();

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAppointments(newValue);
        });

        btnDelete.setDisable(true);
        if (btnDetails != null) btnDetails.setDisable(true);

        tableAppointments.setOnScroll(e -> e.consume());
    }

    private void loadPatientDropdown() {
        patientSelectorList.clear();
        List<Patient> patients = patientDAO.getAllPatients();
        patientSelectorList.addAll(patients);
        cmbPatient.setItems(patientSelectorList);
    }

    private void refreshAppointmentTable() {
        appointmentList.clear();
        List<Appointment> appts = appointmentDAO.getAllAppointments();
        appointmentList.addAll(appts);
        tableAppointments.setItems(appointmentList);
    }

    private void filterAppointments(String query) {
        if (query == null || query.trim().isEmpty()) {
            refreshAppointmentTable();
        } else {
            appointmentList.clear();
            List<Appointment> results = appointmentDAO.searchAppointments(query.trim());
            appointmentList.addAll(results);
            tableAppointments.setItems(appointmentList);
        }
    }

    // Enregistre ou met à jour un rendez-vous
    @FXML
    void handleSaveAppointment(ActionEvent event) {
        Patient patient = cmbPatient.getValue();
        String doctorName = txtDoctorName.getText().trim();
        LocalDate date = dpDate.getValue();
        String hour = cmbHour.getValue();
        String minute = cmbMinute.getValue();
        String description = txtDescription.getText().trim();

        if (patient == null || doctorName.isEmpty() || date == null || hour == null || minute == null) {
            showAlert(AlertType.WARNING, "Formulaire Incomplet", "Veuillez remplir tous les champs obligatoires pour planifier.");
            return;
        }

        LocalTime time = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute));
        LocalDateTime appointmentDateTime = LocalDateTime.of(date, time);

        if (selectedAppointment == null) {
            Appointment newAppt = new Appointment(appointmentDateTime, patient.getId(), doctorName, description);
            boolean success = appointmentDAO.addAppointment(newAppt);
            if (success) {
                showAlert(AlertType.INFORMATION, "Succès", "Rendez-vous planifié avec succès.");
                handleClearForm(null);
                refreshAppointmentTable();
            } else {
                showAlert(AlertType.ERROR, "Erreur", "Impossible d'enregistrer le rendez-vous.");
            }
        } else {
            selectedAppointment.setPatientId(patient.getId());
            selectedAppointment.setDoctorName(doctorName);
            selectedAppointment.setAppointmentDate(appointmentDateTime);
            selectedAppointment.setDescription(description);

            boolean success = appointmentDAO.updateAppointment(selectedAppointment);
            if (success) {
                showAlert(AlertType.INFORMATION, "Succès", "Rendez-vous mis à jour avec succès.");
                handleClearForm(null);
                refreshAppointmentTable();
            } else {
                showAlert(AlertType.ERROR, "Erreur", "Impossible de mettre à jour le rendez-vous.");
            }
        }
    }

    // Gère la sélection d'une ligne dans le tableau
    @FXML
    void handleTableClick(MouseEvent event) {
        selectedAppointment = tableAppointments.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            txtDoctorName.setText(selectedAppointment.getDoctorName());
            txtDescription.setText(selectedAppointment.getDescription());
            
            LocalDateTime ldt = selectedAppointment.getAppointmentDate();
            dpDate.setValue(ldt.toLocalDate());
            
            String hourStr = String.format("%02d", ldt.getHour());
            String minStr = String.format("%02d", ldt.getMinute());
            cmbHour.setValue(hourStr);
            cmbMinute.setValue(minStr);

            for (Patient p : patientSelectorList) {
                if (p.getId() == selectedAppointment.getPatientId()) {
                    cmbPatient.setValue(p);
                    break;
                }
            }

            btnSave.setText("Mettre à jour");
            btnDelete.setDisable(false);
            if (btnDetails != null) btnDetails.setDisable(false);
        }
    }

    // Réinitialise le formulaire
    @FXML
    void handleClearForm(ActionEvent event) {
        cmbPatient.setValue(null);
        txtDoctorName.clear();
        dpDate.setValue(null);
        cmbHour.setValue(null);
        cmbMinute.setValue(null);
        txtDescription.clear();
        tableAppointments.getSelectionModel().clearSelection();

        selectedAppointment = null;
        btnSave.setText("Enregistrer");
        btnDelete.setDisable(true);
        loadPatientDropdown();
    }

    // Supprime le rendez-vous sélectionné
    @FXML
    void handleDeleteAppointment(ActionEvent event) {
        if (selectedAppointment == null) {
            showAlert(AlertType.WARNING, "Aucun rendez-vous sélectionné", "Sélectionnez une ligne à supprimer.");
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/confirm-dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            ConfirmDialogController ctrl = loader.getController();
            ctrl.setMessage("Supprimer le rendez-vous du " + selectedAppointment.getAppointmentDate().format(DATE_TIME_FORMATTER) + " ?");

            javafx.stage.Stage dialog = new javafx.stage.Stage();
            dialog.initOwner(txtDoctorName.getScene().getWindow());
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();

            if (ctrl.isConfirmed()) {
                boolean success = appointmentDAO.deleteAppointment(selectedAppointment.getId());
                if (success) {
                    showAlert(AlertType.INFORMATION, "Succès", "Rendez-vous annulé et supprimé.");
                    handleClearForm(null);
                    refreshAppointmentTable();
                } else {
                    showAlert(AlertType.ERROR, "Erreur", "Impossible de supprimer le rendez-vous.");
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Impossible d'ouvrir la confirmation.");
        }
    }

    // Affiche les détails du rendez-vous
    @FXML
    void handleDetailsAppointment(ActionEvent event) {
        try {
            Appointment a = tableAppointments.getSelectionModel().getSelectedItem();
            if (a == null) {
                showAlert(AlertType.WARNING, "Aucun rendez-vous sélectionné", "Sélectionnez une ligne pour voir les détails.");
                return;
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/modal-dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            ModalController ctrl = loader.getController();

            StringBuilder sb = new StringBuilder();
            sb.append("ID: ").append(a.getId()).append("\n");
            sb.append("Date: ").append(a.getAppointmentDate().format(DATE_TIME_FORMATTER)).append("\n");
            sb.append("Patient ID: ").append(a.getPatientId()).append("\n");
            sb.append("Médecin: ").append(a.getDoctorName()).append("\n");
            sb.append("Description: ").append(a.getDescription()).append("\n");

            ctrl.setContent("Rendez-vous " + a.getId(), sb.toString());

            javafx.stage.Stage dialog = new javafx.stage.Stage();
            dialog.initOwner(txtDoctorName.getScene().getWindow());
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            dialog.setScene(scene);
            dialog.setTitle("Détails du rendez-vous");
            dialog.setResizable(false);
            dialog.showAndWait();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de détails.");
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
