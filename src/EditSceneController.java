import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class EditSceneController {

    @FXML private AnchorPane leftPane;
    @FXML private VBox logoList;
    private HBox currHbox;
    private int ELEMENTS_IN_LOGO_LIST = 0;
    private final int LOGO_SIZE = 100;



    @FXML
    public void setMainImage(Image image) {
        ImageView iv = new ImageView(image);
        iv.setSmooth(true);
        leftPane.getChildren().add(iv);
    }

    @FXML
    private void addLogoToMainImage(Image logo) {
        ImageView iv = new ImageView(logo);
        iv.setSmooth(true); // utilizes a slower but better image quality algorithm when resizing images

        Pane pane = new AnchorPane(iv);
        scalePane(pane, logo);

        //fits the image to the parent pane
        iv.fitHeightProperty().bind(pane.heightProperty());
        iv.fitWidthProperty().bind(pane.widthProperty());

        DragResizerPane.makeResizable(pane);
        leftPane.getChildren().add(pane);
    }

    @FXML
    public void addLogoToLogoList(Image logo) {
        ImageView iv = new ImageView(logo);
        iv.setPreserveRatio(true);
        iv.setFitHeight(LOGO_SIZE);

        //wraps image in pane to add padding
        Pane pane = new AnchorPane(iv);
        pane.setPadding(new Insets(5));
        pane.setOnMousePressed(event -> addLogoToMainImage(logo));

        if (ELEMENTS_IN_LOGO_LIST % 3 == 0) {
            currHbox = new HBox();
            logoList.getChildren().add(currHbox);
        }
        currHbox.getChildren().add(pane);
        ELEMENTS_IN_LOGO_LIST += 1;
    }

    @FXML
    public void closeProgram(){
        LogoEditor.mainStage.close();
    }

    @FXML
    public void uploadLogo(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(LogoEditor.mainStage);
        if (selectedFile != null) {
            try {
                Image img = img = new Image(selectedFile.toURI().toString());
                addLogoToLogoList(img);
            } catch (Exception e){
                //TODO: Her skal printes at billedet ikke var png eller jpg
            }
        }
    }

    @FXML
    public void saveImage(){
        WritableImage img = leftPane.snapshot(new SnapshotParameters(), null);
        File file = new File("src\\outputImages\\savedImage.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //scales a pane that wraps an image to
    private void scalePane(Pane pane, Image image) {
        double prevWidth = image.getWidth();
        double prevHeight = image.getHeight();
        double newWidth = LOGO_SIZE;
        pane.setMinWidth(newWidth);
        pane.setMaxWidth(newWidth);
        pane.setMinHeight(prevHeight * (newWidth / prevWidth));
        pane.setMaxHeight(prevHeight * (newWidth / prevWidth));
    }
}
