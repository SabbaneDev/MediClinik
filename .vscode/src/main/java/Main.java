import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.DBConnection;
import atlantafx.base.theme.PrimerLight;

// Point d'entrée de l'application
public class Main extends Application {

    // Initialise et affiche la fenêtre principale
    @Override
    public void start(Stage primaryStage) throws Exception {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        primaryStage.initStyle(StageStyle.TRANSPARENT);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);
        scene.setFill(Color.TRANSPARENT);

        final double[] drag = {0, 0};
        javafx.scene.Node titleBar = root.lookup("#titleBar");
        if (titleBar != null) {
            titleBar.setOnMousePressed(e -> {
                drag[0] = e.getSceneX();
                drag[1] = e.getSceneY();
            });
            titleBar.setOnMouseDragged(e -> {
                if (!primaryStage.isMaximized()) {
                    primaryStage.setX(e.getScreenX() - drag[0]);
                    primaryStage.setY(e.getScreenY() - drag[1]);
                }
            });
        }

        primaryStage.maximizedProperty().addListener((obs, wasMax, isMax) -> {
            if (isMax) {
                Rectangle2D vb = Screen.getPrimary().getVisualBounds();
                primaryStage.setX(vb.getMinX());
                primaryStage.setY(vb.getMinY());
                primaryStage.setWidth(vb.getWidth());
                primaryStage.setHeight(vb.getHeight());
            }
        });

        primaryStage.setTitle("MEDI-CLINIC");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    // Fermer la connexion à la base de données à l'arrêt
    @Override
    public void stop() {
        System.out.println("Stopping application…");
        DBConnection.closeConnection();
    }

    // Lance l'application
    public static void main(String[] args) {
        launch(args);
    }
}
