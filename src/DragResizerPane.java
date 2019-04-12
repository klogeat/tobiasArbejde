import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class DragResizerPane {
    private static double orgSceneX, orgSceneY;
    private static double orgTranslateX, orgTranslateY;

    private static final int RESIZE_MARGIN = 6;

    private final Pane pane;

    private int dragging = 0;

    private final int NOTDRAGGING = 0;
    private final int SOUTH = 1;
    private final int EAST = 2;
    private final int SOUTHEAST = 3;

    private DragResizerPane(Pane inputPane) {
        pane = inputPane;
    }

    public static void makeResizable(Pane pane) {
        final DragResizerPane resizer = new DragResizerPane(pane);
        pane.setOnMousePressed(resizer::mousePressed);
        pane.setOnMouseDragged(resizer::mouseDragged);
        pane.setOnMouseMoved(resizer::mouseOver);
        pane.setOnMouseReleased(resizer::mouseReleased);
    }

    private void mouseReleased(MouseEvent event) {
        dragging = NOTDRAGGING;
        pane.setCursor(Cursor.DEFAULT);
    }

    private void mouseOver(MouseEvent event) {
        if (isInDraggableZoneSE(event) || dragging == SOUTHEAST) {
            pane.setCursor(Cursor.SE_RESIZE);
        } else if (isInDraggableZoneS(event) || dragging == SOUTH) {
            pane.setCursor(Cursor.S_RESIZE);
        } else if (isInDraggableZoneE(event) || dragging == EAST) {
            pane.setCursor(Cursor.E_RESIZE);
        } else {
            pane.setCursor(Cursor.DEFAULT);
        }
    }

    private boolean isInDraggableZoneSE(MouseEvent event) { return (event.getX() > (pane.getMinWidth() - RESIZE_MARGIN)) && (event.getY() > (pane.getMinHeight() - RESIZE_MARGIN)); }

    private boolean isInDraggableZoneS(MouseEvent event) { return event.getY() > (pane.getMinHeight() - RESIZE_MARGIN); }

    private boolean isInDraggableZoneE(MouseEvent event) { return event.getX() > (pane.getMinWidth() - RESIZE_MARGIN); }

    private void mouseDragged(MouseEvent event) {
        //if we are dragging in a direction
        if (dragging == SOUTHEAST) {
            dragSouth(event);
        } else if (dragging == SOUTH) {
            dragSouth(event);
        } else if (dragging == EAST) {
            dragEast(event);
        }
        //if we are drag and dropping the pane
        else {
            double offsetX = event.getSceneX() - orgSceneX;
            double offsetY = event.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            ((Pane)(event.getSource())).setTranslateX(newTranslateX);
            ((Pane)(event.getSource())).setTranslateY(newTranslateY);
        }
    }

    private void dragSouth(MouseEvent event){
        double prevHeight = pane.getMinHeight();
        double newHeight = event.getY();
        if(prevHeight <= 5 && newHeight < prevHeight) {
            //image cannot get smaller
        } else {
            setHeight(newHeight);
            setWidth(pane.getMinWidth() * (newHeight/prevHeight));
        }
    }

    private void dragEast(MouseEvent event){
        double prevWidth = pane.getMinWidth();
        double newWidth = event.getX();
        if(prevWidth <= 5 && newWidth < prevWidth) {
            //image cannot get smaller
        } else {
            setWidth(event.getX());
            setHeight(pane.getMinHeight() * (pane.getMinWidth()/prevWidth));
        }
    }

    private void mousePressed(MouseEvent event) {
        //if we are trying to resize
        if (isInDraggableZoneSE(event)) {
            dragging = SOUTHEAST;
        } else if (isInDraggableZoneE(event)) {
            dragging = EAST;
        } else if (isInDraggableZoneS(event)) {
            dragging = SOUTH;
        }
        //if we are trying to drag and drop
        else {
            orgSceneX = event.getSceneX();
            orgSceneY = event.getSceneY();
            orgTranslateX = ((Pane)(event.getSource())).getTranslateX();
            orgTranslateY = ((Pane)(event.getSource())).getTranslateY();
        }
    }

    private void setHeight(double height) {
        pane.setMinHeight(height);
        pane.setMaxHeight(height);
    }
    private void setWidth(double height) {
        pane.setMinWidth(height);
        pane.setMaxWidth(height);
    }

}