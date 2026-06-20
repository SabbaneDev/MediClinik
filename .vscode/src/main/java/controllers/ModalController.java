package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

// Contrôleur pour la boîte de dialogue modale
public class ModalController {

    @FXML private Label lblTitle;
    @FXML private TextArea txtContent;
    @FXML private Button btnClose;

    // Définit le contenu de la boîte modale
    public void setContent(String title, String content) {
        lblTitle.setText(title != null ? title : "");
        txtContent.setText(content != null ? content : "");
    }

    @FXML
    void handleClose(ActionEvent event) {
        Stage s = (Stage) btnClose.getScene().getWindow();
        s.close();
    }
}
