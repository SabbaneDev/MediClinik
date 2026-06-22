package controllers;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.Patient;
import models.PatientDAO;

// Gèrer la gestion des patients
public class PatientController {

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtAge;

    @FXML
    private ComboBox<String> cmbGender;

    @FXML
    private TextField txtPhoneNumber;

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
    private TableView<Patient> tablePatients;

    @FXML
    private TableColumn<Patient, Integer> colId;

    @FXML
    private TableColumn<Patient, String> colFirstName;

    @FXML
    private TableColumn<Patient, String> colLastName;

    @FXML
    private TableColumn<Patient, Integer> colAge;

    @FXML
    private TableColumn<Patient, String> colGender;

    @FXML
    private TableColumn<Patient, String> colPhoneNumber;

    private final PatientDAO patientDAO = new PatientDAO();
    private final ObservableList<Patient> patientList = FXCollections.observableArrayList();
    
    private Patient selectedPatient = null;

    // Initialise la vue des patients
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        refreshPatientTable();

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterPatients(newValue);
        });

        btnDelete.setDisable(true);
        if (btnDetails != null) btnDetails.setDisable(true);

        tablePatients.setOnScroll(e -> e.consume());
    }

    private void refreshPatientTable() {
        patientList.clear();
        List<Patient> patients = patientDAO.getAllPatients();
        patientList.addAll(patients);
        tablePatients.setItems(patientList);
    }

    private void filterPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            refreshPatientTable();
        } else {
            patientList.clear();
            List<Patient> results = patientDAO.searchPatients(query.trim());
            patientList.addAll(results);
            tablePatients.setItems(patientList);
        }
    }

    // Enregistrer ou met à jour un patient
    @FXML
    void handleSavePatient(ActionEvent event) {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String ageStr = txtAge.getText().trim();
        String gender = cmbGender.getValue();
        String phoneNumber = txtPhoneNumber.getText().trim();

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || ageStr.isEmpty() || gender == null || phoneNumber.isEmpty()) {
            showAlert(AlertType.WARNING, "Formulaire Incomplet", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 150) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Âge Invalide", "Veuillez saisir un âge numérique valide compris entre 1 et 150 ans.");
            return;
        }

        if (selectedPatient == null) {
            Patient newPatient = new Patient(firstName, lastName, age, phoneNumber, gender);
            boolean success = patientDAO.addPatient(newPatient);
            if (success) {
                showAlert(AlertType.INFORMATION, "Succès", "Patient enregistré avec succès.");
                handleClearForm(null);
                refreshPatientTable();
            } else {
                showAlert(AlertType.ERROR, "Erreur", "Impossible de sauvegarder le patient.");
            }
        } else {
            selectedPatient.setFirstName(firstName);
            selectedPatient.setLastName(lastName);
            selectedPatient.setAge(age);
            selectedPatient.setGender(gender);
            selectedPatient.setPhoneNumber(phoneNumber);

            boolean success = patientDAO.updatePatient(selectedPatient);
            if (success) {
                showAlert(AlertType.INFORMATION, "Succès", "Informations du patient mises à jour avec succès.");
                handleClearForm(null);
                refreshPatientTable();
            } else {
                showAlert(AlertType.ERROR, "Erreur", "Impossible de mettre à jour les informations du patient.");
            }
        }
    }

    // Gèrer la sélection d'une ligne dans le tableau
    @FXML
    void handleTableClick(MouseEvent event) {
        selectedPatient = tablePatients.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            txtFirstName.setText(selectedPatient.getFirstName());
            txtLastName.setText(selectedPatient.getLastName());
            txtAge.setText(String.valueOf(selectedPatient.getAge()));
            cmbGender.setValue(selectedPatient.getGender());
            txtPhoneNumber.setText(selectedPatient.getPhoneNumber());
            
            btnSave.setText("Mettre à jour");
            btnDelete.setDisable(false);
            if (btnDetails != null) btnDetails.setDisable(false);
        }
    }

    // Afficher les détails du patient
    @FXML
    void handleDetailsPatient(ActionEvent event) {
        try {
            Patient p = tablePatients.getSelectionModel().getSelectedItem();
            if (p == null) {
                showAlert(AlertType.WARNING, "Aucun patient sélectionné", "Sélectionnez une ligne pour voir les détails.");
                return;
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/modal-dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            ModalController ctrl = loader.getController();

            StringBuilder sb = new StringBuilder();
            sb.append("ID: ").append(p.getId()).append("\n");
            sb.append("Prénom: ").append(p.getFirstName()).append("\n");
            sb.append("Nom: ").append(p.getLastName()).append("\n");
            sb.append("Âge: ").append(p.getAge()).append("\n");
            sb.append("Genre: ").append(p.getGender()).append("\n");
            sb.append("Téléphone: ").append(p.getPhoneNumber()).append("\n");

            ctrl.setContent(p.getFirstName() + " " + p.getLastName(), sb.toString());

            javafx.stage.Stage dialog = new javafx.stage.Stage();
            dialog.initOwner(txtFirstName.getScene().getWindow());
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            dialog.setScene(scene);
            dialog.setTitle("Détails du patient");
            dialog.setResizable(false);
            dialog.showAndWait();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de détails.");
        }
    }

    // Réinitialiser le formulaire
    @FXML
    void handleClearForm(ActionEvent event) {
        txtFirstName.clear();
        txtLastName.clear();
        txtAge.clear();
        cmbGender.setValue(null);
        txtPhoneNumber.clear();
        tablePatients.getSelectionModel().clearSelection();
        
        selectedPatient = null;
        btnSave.setText("Enregistrer");
        btnDelete.setDisable(true);
    }

    // Supprimer le patient sélectionné
    @FXML
    void handleDeletePatient(ActionEvent event) {
        if (selectedPatient == null) {
            showAlert(AlertType.WARNING, "Aucun patient sélectionné", "Sélectionnez une ligne à supprimer.");
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/confirm-dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            ConfirmDialogController ctrl = loader.getController();
            ctrl.setMessage("Supprimer le patient '" + selectedPatient.getFirstName() + " " + selectedPatient.getLastName() + "' ? Cette action est irréversible.");

            javafx.stage.Stage dialog = new javafx.stage.Stage();
            dialog.initOwner(txtFirstName.getScene().getWindow());
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();

            if (ctrl.isConfirmed()) {
                boolean success = patientDAO.deletePatient(selectedPatient.getId());
                if (success) {
                    showAlert(AlertType.INFORMATION, "Succès", "Le patient et ses rendez-vous ont été supprimés.");
                    handleClearForm(null);
                    refreshPatientTable();
                } else {
                    showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de la suppression.");
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Impossible d'ouvrir la confirmation.");
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
