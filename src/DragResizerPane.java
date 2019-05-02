import javafx.scene.Cursor;
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
    private final int NORTHMIDDLE = 4;

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
        } else if (isInDraggableZoneNM(event) || dragging == NORTHMIDDLE) {
            pane.setCursor(Cursor.OPEN_HAND);
        } else {
            pane.setCursor(Cursor.MOVE);
        }
    }

    private boolean isInDraggableZoneSE(MouseEvent event) { return (event.getX() > (pane.getMinWidth() - RESIZE_MARGIN)) && (event.getY() > (pane.getMinHeight() - RESIZE_MARGIN)); }

    private boolean isInDraggableZoneNM(MouseEvent event) { return (event.getX() <= middleX() *1.3 && event.getX() >= middleX() * 0.7  && event.getY() < middleY() * 0.6); }

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
        } else if (dragging == NORTHMIDDLE){
            rotate(event);
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

    private void rotate(MouseEvent event){
        pane.setRotate(0);
        double rotateAmount = calculateAngle(event);
        pane.setRotate(rotateAmount);
    }
    private double calculateAngle(MouseEvent event) {
        double x1 = pane.getTranslateX() + middleX();
        double y1 = pane.getTranslateY() + middleY();
        double x2 = event.getSceneX();
        double y2 = event.getSceneY();
        double side1 = Math.abs(x2 - x1);
        double side2 = Math.abs(y2 - y1);
        double hypotenuse = Math.sqrt(side1 * side1 + side2 * side2);
        double angle = Math.asin(side2 / hypotenuse) * (180 / Math.PI);
        if (x2 >= x1 && y2 <= y1) {
            return 90 - angle;
        } else if (x2 >= x1 && y2 >= y1){
            return angle + 90;
        } else if (x2 <= x1 && y2 >= y1){
            return 270 - angle;
        } else {
            return angle + 270;
        }
    }

    private double middleX(){
        return pane.getWidth()/2;
    }

    private double middleY(){
        return pane.getHeight()/2;
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
        } else if (isInDraggableZoneNM(event)){
            dragging = NORTHMIDDLE;
            pane.setCursor(Cursor.CLOSED_HAND);
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