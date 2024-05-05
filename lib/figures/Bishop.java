package lib.figures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.scene.paint.Color;
import lib.figures.helper.Graphics;
import lib.interfaces.Figure;
import lib.logic.Position;

public class Bishop extends Figure {

    public static final int GRID_SIZE = 8;

    public Bishop(Color color, Position position) {
        super(Graphics.buildImagePath(color, "bishop"), color, position);
        name = "bishop";
    }

    @Override
    public List<Position> getPossibleMoves(final Map<Position, Figure> figures) {
        List<Position> moves = new ArrayList<>();

        // Check all four diagonal directions
        int[] directionsX = {1, 1, -1, -1};
        int[] directionsY = {1, -1, 1, -1};

        // moves.add(new Position(getX(), getY()));

        for (int i = 0; i < directionsX.length; i++) {
            int currentX = getX();
            int currentY = getY();

            while (true) {
                currentX += directionsX[i];
                currentY += directionsY[i];

                if (currentX < 0 || currentX >= GRID_SIZE || currentY < 0 || currentY >= GRID_SIZE) {
                    break; // Break if out of bounds
                }

                Position newPosition = new Position(currentX, currentY);
                if (figures.containsKey(newPosition)) {
                    Figure otherFigure = figures.get(newPosition);
                    if (otherFigure.getColor() != this.getColor()) {
                        moves.add(new Position(currentX, currentY)); // Can capture an opponent's piece
                    }
                    break; // Stop searching this diagonal
                } else {
                    moves.add(new Position(currentX, currentY)); // Empty spot, add as valid move
                }
            }
        }

        return moves;
    }

}
