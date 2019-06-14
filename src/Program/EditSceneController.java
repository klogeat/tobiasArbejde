package Program;

import Program.DragResizerPane;
import Program.LogoEditor;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public class EditSceneController {

    @FXML private AnchorPane leftPane;
    @FXML private HBox logoList;
    private VBox currHbox;
    private int ELEMENTS_IN_LOGO_LIST = 0;
    private final int LOGO_SIZE = 100;
    private final double MAIN_IMAGE_MAX_WIDTH = 718;
    private final double MAIN_IMAGE_MAX_HEIGHT = 718;

    private ImageView primaryImage;

    private String destination;
    private String pathForLastGeneratedPicture;

    public void setCacheDestination(String destination) {
        this.destination = destination;
    }

    @FXML public void setMainImage(Image image) {
        this.primaryImage = new ImageView(image);
        primaryImage.setSmooth(true);
        primaryImage.setFitWidth(image.getWidth());
        primaryImage.setFitHeight(image.getHeight());

        //Scales image to pane size if image is too large
        scaleMainImage(primaryImage);
        //Scales pane to image size for better snapshots
        leftPane.setMaxSize(primaryImage.getFitWidth(), primaryImage.getFitHeight());
        leftPane.getChildren().add(primaryImage);
    }

    private void scaleMainImage(ImageView iv) {
        if(iv.getFitHeight() > MAIN_IMAGE_MAX_HEIGHT){
            double scaleFactor = MAIN_IMAGE_MAX_HEIGHT / iv.getFitHeight();
            iv.setFitHeight(MAIN_IMAGE_MAX_HEIGHT);
            iv.setFitWidth(iv.getFitWidth() * scaleFactor);
        }
        if(iv.getFitWidth() > MAIN_IMAGE_MAX_WIDTH){
            double scaleFactor = MAIN_IMAGE_MAX_WIDTH / iv.getFitWidth();
            iv.setFitWidth(MAIN_IMAGE_MAX_WIDTH);
            iv.setFitHeight(iv.getFitHeight() * scaleFactor);
        }
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
        makeDeletable(pane);
        leftPane.getChildren().add(pane);
    }

    private void makeDeletable(Pane pane){
        pane.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.SECONDARY)){
                leftPane.getChildren().remove(pane);
            }
        });
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

        if (ELEMENTS_IN_LOGO_LIST % 6 == 0) {
            currHbox = new VBox();
            logoList.getChildren().add(currHbox);
        }
        currHbox.getChildren().add(pane);
        ELEMENTS_IN_LOGO_LIST += 1;
    }

    @FXML
    public void closeProgram(){
        ((Stage) leftPane.getScene().getWindow()).close();
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
                Image img = new Image(selectedFile.toURI().toString());
                addLogoToLogoList(img);
            } catch (Exception e){
                //TODO: Her skal printes at billedet ikke var png eller jpg
            }
        }
    }

    @FXML
    public void saveImage() {

        if(destination == null) throw new IllegalArgumentException("Please set cache destination for created images");

        final int DPI_SCALE = 4;
        leftPane.autosize();

        Bounds bounds = leftPane.getLayoutBounds();
        WritableImage image = new WritableImage(
                (int) Math.round(bounds.getWidth() * DPI_SCALE),
                (int) Math.round(bounds.getHeight() * DPI_SCALE));

        SnapshotParameters spa = new SnapshotParameters();
        spa.setTransform(javafx.scene.transform.Transform.scale(DPI_SCALE, DPI_SCALE));
        image = leftPane.snapshot(spa, image);
        // We place the image in a unique folder (timestamp)
        String filename = getFilename(primaryImage);
        String timestamp = getCurrentTimeStamp() + "";
        File file = new File(destination + "/" + timestamp + "/" + filename + ".png");
        try {
            // Clumsy but will have to do for now
            // We are creating the directory for the image
            new File(destination + "/" + timestamp).mkdir();
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            this.pathForLastGeneratedPicture = file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFilename(ImageView imageView) {
        String url = imageView.getImage().getUrl();
        return url.substring(url.lastIndexOf("/"), url.lastIndexOf("."));
    }

    private long getCurrentTimeStamp() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+1")).getTime().getTime();
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

    public String getPathForLastGeneratedPicture() {
        return pathForLastGeneratedPicture;
    }
}
