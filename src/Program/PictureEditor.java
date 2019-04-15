package Program;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class PictureEditor {

    private Stage stage;
    private FXMLLoader loader;
    private EditSceneController controller;

    public PictureEditor(Image primary, Image ... logoPictures) throws IOException {
        this.stage = new Stage();
        this.loader = new FXMLLoader(getClass().getResource("/Program/EditScene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        controller = loader.getController();
        controller.setMainImage(primary);
        for (Image logo : logoPictures)
            controller.addLogoToLogoList(logo);
    }

    public void setCacheDestination(String destination) {
        controller.setCacheDestination(destination);
    }

    public String showAndWait() {
        // "Clumsy but will have to do for now."
        // Launches stage and returns generated picture when closed
        stage.showAndWait();
        return controller.getPathForLastGeneratedPicture();
    }

    public static void main(String[] args) {}


}
