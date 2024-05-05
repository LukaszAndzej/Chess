package lib.figures;

import lib.figures.helper.Graphics;
import lib.interfaces.Figure;
import lib.logic.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.scene.paint.Color;

public class Rook extends Figure {

    public static final int GRID_SIZE = 8;

    public Rook(Color color, Position position) {
        super(Graphics.buildImagePath(color, "rook"), color, position);
        name = "rook";
    }

    @Override
    public List<Position> getPossibleMoves(final Map<Position, Figure> figures) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1} // Góra, dół, prawo, lewo
        };

        for (int[] dir : directions) {
            int currentX = this.position.getX();
            int currentY = this.position.getY();
            while (true) {
                currentX += dir[0];
                currentY += dir[1];

                if (currentX < 0 || currentX >= GRID_SIZE || currentY < 0 || currentY >= GRID_SIZE) {
                    break;
                }

                Position newPosition = new Position(currentX, currentY);
                if (figures.containsKey(newPosition)) {
                    Figure figure = figures.get(newPosition);
                    if (figure.getColor() != this.color) {
                        moves.add(newPosition); // Możliwe bicie
                    }
                    break;
                } else {
                    moves.add(newPosition); // Dodaj puste pole
                }
            }
        }

        return moves;
    }

}
