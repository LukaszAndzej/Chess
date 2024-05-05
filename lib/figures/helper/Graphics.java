package lib.figures.helper;

import javafx.scene.paint.Color;

public class Graphics {

    public static final int GRID_SIZE = 8;

    private Graphics() {
        throw new AssertionError("Just Helper!");
    }

    public static String buildImagePath(Color color, String figureName) {
        String colorFolder = (color == Color.WHITE) ? "white" : "black";
        return "file:pic/" + colorFolder + "/" + figureName + ".png";
    }

}
