package Program;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class LogoEditor extends Application {
    public static Stage mainStage;
    private FXMLLoader loader = new FXMLLoader(getClass().getResource("/Program/EditScene.fxml"));

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        Parent root = loader.load();
        stage.setScene(new Scene(root, 1080, 720));
        stage.setTitle("Tilføj logo'er");

        EditSceneController controller = loader.getController();
        controller.setMainImage(new Image("testImages/114041-271infrafront-p.png"));
        controller.addLogoToLogoList(new Image("outputImages/instaLogo.png"));
        controller.addLogoToLogoList(new Image("outputImages/logo.png"));
        controller.addLogoToLogoList(new Image("outputImages/logo.png"));
        controller.addLogoToLogoList(new Image("outputImages/logo.png"));
        controller.addLogoToLogoList(new Image("outputImages/logo.png"));
        controller.addLogoToLogoList(new Image("outputImages/logo.png"));
        controller.addLogoToLogoList(new Image("outputImages/logo.png"));
        controller.addLogoToLogoList(new Image("outputImages/logo.png"));

        controller.setCacheDestination("src/outputImages/");

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}