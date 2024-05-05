package lib.figures;

import lib.figures.helper.Graphics;
import lib.interfaces.Figure;
import lib.logic.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.scene.paint.Color;

public class Knight extends Figure {
    public static final int GRID_SIZE = 8;

    public Knight(Color color, Position position) {
        super(Graphics.buildImagePath(color, "knight"), color, position);
        name = "knight";
    }

    @Override
    public List<Position> getPossibleMoves(final Map<Position, Figure> figures) {
        List<Position> moves = new ArrayList<>();
        int[][] movesOffsets = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        int currentX = this.position.getX();
        int currentY = this.position.getY();

        for (int[] offset : movesOffsets) {
            int newX = currentX + offset[0];
            int newY = currentY + offset[1];

            if (newX >= 0 && newX < GRID_SIZE && newY >= 0 && newY < GRID_SIZE) {
                if (isMoveLegal(figures, newX, newY)) {
                    moves.add(new Position(newX, newY));
                }
            }
        }

        return moves;
    }

    private boolean isMoveLegal(Map<Position, Figure> figures, int x, int y) {
        Position newPosition = new Position(x, y);
        if (figures.containsKey(newPosition)) {
            Figure figure = figures.get(newPosition);
            if (figure.getColor() == this.color) {
                return false; // figurę tego samego koloru
            }
        }
        return true; // Pole jest puste lub zajmowane przez przeciwnika
    }

}