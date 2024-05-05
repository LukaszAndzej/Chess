package lib.figures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.scene.paint.Color;
import lib.figures.helper.Graphics;
import lib.interfaces.Figure;
import lib.logic.Position;

public class Pawn extends Figure {
    public static final int GRID_SIZE = 8;

    public Pawn(Color color, Position position) {
        super(Graphics.buildImagePath(color, "pawn"), color, position);
        name = "pawn";
    }

    @Override
    public List<Position> getPossibleMoves(Map<Position, Figure> figures) {
        List<Position> possibleMoves = new ArrayList<>();
        int x = this.position.getX();
        int y = this.position.getY();

        int forwardDirection = this.color == Color.WHITE ? 1 : -1; // White moves up, Black moves down

        // Check for standard one step forward move
        int newY = y + forwardDirection;
        if (newY >= 0 && newY < GRID_SIZE && isSquareEmpty(figures, x, newY)) {
            possibleMoves.add(new Position(x, newY));

            // Check for two steps move on first move
            if ((this.color == Color.WHITE && y == 1) || (this.color == Color.BLACK && y == 6)) {
                int twoStepY = newY + forwardDirection;
                if (twoStepY >= 0 && twoStepY < GRID_SIZE && isSquareEmpty(figures, x, twoStepY)) {
                    possibleMoves.add(new Position(x, twoStepY));
                }
            }
        }

        // Add logic for capturing moves diagonally
        int[] captureDirectionsX = {-1, 1}; // Diagonal captures to the left and right
        for (int dx : captureDirectionsX) {
            int captureX = x + dx;
            int captureY = newY; // same row as the standard move forward
            if (captureX >= 0 && captureX < GRID_SIZE && captureY >= 0 && captureY < GRID_SIZE && isEnemyPiece(figures, captureX, captureY)) {
                possibleMoves.add(new Position(captureX, captureY));
            }
        }

        return possibleMoves;
    }

    private boolean isSquareEmpty(final Map<Position, Figure> figures, int x, int y) {
        return !figures.containsKey(new Position(x, y));
    }

    private boolean isEnemyPiece(final Map<Position, Figure> figures, int x, int y) {
        Figure figure = figures.get(new Position(x, y));
        return figure != null && figure.getColor() != this.color;
    }

}
