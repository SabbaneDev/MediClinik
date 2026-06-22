package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

// Contrôleur pour la boîte de confirmation
public class ConfirmDialogController {

    @FXML private Label lblMessage;
    @FXML private Button btnConfirm;
    @FXML private Button btnCancel;

    private boolean confirmed = false;

    // Définit le message à afficher
    public void setMessage(String message) {
        lblMessage.setText(message != null ? message : "");
    }

    @FXML
    void handleConfirm(ActionEvent event) {
        confirmed = true;
        close();
    }

    @FXML
    void handleCancel(ActionEvent event) {
        confirmed = false;
        close();
    }

    private void close() {
        Stage s = (Stage) lblMessage.getScene().getWindow();
        s.close();
    }

    // Retourne l'état de la confirmation
    public boolean isConfirmed() {
        return confirmed;
    }
}
