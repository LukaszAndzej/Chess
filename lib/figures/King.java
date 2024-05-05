package lib.figures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.scene.paint.Color;
import lib.figures.helper.Graphics;
import lib.interfaces.Figure;
import lib.logic.Position;

public class King extends Figure {

    public static final int GRID_SIZE = 8;

    public King(Color color, Position position) {
        super(Graphics.buildImagePath(color, "king"), color, position);
        name = "king";
    }


    @Override
    public List<Position> getPossibleMoves(final Map<Position, Figure> figures) {
        List<Position> moves = new ArrayList<>();
        int[] directionX = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] directionY = {-1, 0, 1, -1, 1, -1, 0, 1};

        int currentX = this.position.getX();
        int currentY = this.position.getY();

        // moves.add(new Position(currentX, currentY));

        for (int i = 0; i < directionX.length; i++) {
            int newX = currentX + directionX[i];
            int newY = currentY + directionY[i];

            if (newX >= 0 && newX < GRID_SIZE && newY >= 0 && newY < GRID_SIZE) { // Ensure the move stays within the board limits
                Position newPosition = new Position(newX, newY);
                if (isSquareEmpty(figures, newPosition) || isEnemyPiece(figures, newPosition)) {
                    moves.add(newPosition);
                }
            }
        }

        // Sprawdzanie możliwości roszady
        if (!this.hasMoved()) {
            // Krótka roszada
            Position kingSideRookPosition = new Position(7, currentY);
            if (canCastle(figures, kingSideRookPosition, currentX, currentY, 1)) {
                moves.add(new Position(currentX + 2, currentY));
            }
            // Długa roszada
            Position queenSideRookPosition = new Position(0, currentY);
            if (canCastle(figures, queenSideRookPosition, currentX, currentY, -1)) {
                moves.add(new Position(currentX - 2, currentY));
            }
        }

        return moves;
    }

    private boolean isSquareEmpty(Map<Position, Figure> figures, Position position) {
        return !figures.containsKey(position);
    }

    private boolean isEnemyPiece(Map<Position, Figure> figures, Position position) {
        Figure figure = figures.get(position);
        return figure != null && figure.getColor() != this.getColor();
    }

    private boolean canCastle(Map<Position, Figure> figures, Position rookPosition, int kingX, int kingY, int direction) {
        Figure rook = figures.get(rookPosition);
        if (rook instanceof Rook && !rook.hasMoved()) {
            // Sprawdzanie czy pola pomiędzy królem a wieżą są puste
            for (int x = kingX + direction; x != rookPosition.getX(); x += direction) {
                if (figures.get(new Position(x, kingY)) != null) {
                    return false;
                }
            }
            //! czy król nie jest w szachu na polach, przez które przechodzi
            return true;
        }
        return false;
    }

}
