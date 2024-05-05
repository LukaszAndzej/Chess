package lib.logic;

import lib.figures.Bishop;
import lib.figures.King;
import lib.figures.Knight;
import lib.figures.Pawn;
import lib.figures.Queen;
import lib.figures.Rook;
import lib.interfaces.Figure;

import javafx.scene.paint.Color;

public class FigureFactory {
    public static Figure createFigure(String figureType, Position position, Color color) {
        switch (figureType) {
            case "Pawn":
                return new Pawn(color, position);
            case "Rook":
                return new Rook(color, position);
            case "Knight":
                return new Knight(color, position);
            case "Bishop":
                return new Bishop(color, position);
            case "Queen":
                return new Queen(color, position);
            case "King":
                return new King(color, position);
            default:
                throw new IllegalArgumentException("Unknown figure type: " + figureType);
        }
    }
}
