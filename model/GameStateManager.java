package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lib.figures.King;
import lib.interfaces.Figure;
import lib.logic.Position;

public class GameStateManager {
    private Map<Position, Figure> figures = new HashMap<>();
    private GridPane gridPane;
    private Color currentTurn = Color.WHITE;
    private boolean gameOver = false;

    public GameStateManager(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    public void initializeGame(Map<Position, Figure> initialSetup) {
        if (!figures.isEmpty()) figures.clear();
        figures.putAll(initialSetup);
    }

    public boolean isMoveAllowed(Figure figure, Position newPosition) {
        // First check if it's the turn of the figure's color
        if (figure.getColor() != currentTurn) {
            return false;
        }

        // Retrieve possible moves for the figure
        List<Position> possibleMoves = getPossibleMoves(figure);

        // Check if the new position is within the possible moves
        return possibleMoves.contains(newPosition);
    }

    public void captureFigure(Position position) {
        figures.remove(position); // Usuń figurę z mapy
        Node targetNode = FigureManager.getNode(gridPane, position.getY(), position.getX());
        if (targetNode instanceof ImageView) {
            gridPane.getChildren().remove(targetNode);  // Usuń zbitych
        }
    }

    public boolean executeMove(Figure figure, Position startPosition, Position endPosition) {
        if (isMoveAllowed(figure, endPosition)) {
            figures.remove(startPosition);  // Remove from old position
            
            // Check if there is a capture
            if (figures.containsKey(endPosition)) {
                captureFigure(endPosition);
            }


            figures.remove(startPosition);
            figures.put(endPosition, figure);  // Move to new position
            figure.setPosition(endPosition);  // Update figure's position

            return true;
        }

        return false;
    }

    public boolean checkForCheckmate(Color kingColor, Figure fig, Position newPosition) {
        if (!isKingInCheck(kingColor)) {
            return false; // Król nie jest w szachu
        }


        figures.remove(fig.getPosition());
        figures.put(newPosition, fig);

        ArrayList<Figure> figuresList = figures.values().stream()
                                        .filter(figure -> figure.getColor() == kingColor)
                                        .collect(Collectors.toCollection(ArrayList::new));
    
        for (Figure figure : figuresList) {
            List<Position> possibleMoves = getPossibleMoves(figure);
            Position originalPosition = figure.getPosition();

            for (Position move : possibleMoves) {
                if (originalPosition.equals(move)) continue;

                // Symulacja ruchu
                Figure capturedFigure = figures.remove(originalPosition);

                if (capturedFigure != null) {
                    figures.put(move, figure);
                    figure.setPosition(move);
    
                    boolean stillInCheck = isKingInCheck(kingColor);
    
                    // Cofnięcie ruchu
                    figures.remove(move);
                    figures.put(originalPosition, figure);
                    figure.setPosition(originalPosition);

                    if (!stillInCheck) {
                        return false; // Znaleziono ruch, który eliminuje szach
                    }
                }
            }
        }

        return true; // Brak ruchu eliminującego szach: szach mat
    }

    public boolean isMoveValid(Figure figure, Position endPosition) {
        // Example: Just check if endPosition is within bounds (you will replace this with real rules)
        return endPosition.getX() >= 0 && endPosition.getX() < 8 &&
                endPosition.getY() >= 0 && endPosition.getY() < 8;
    }

    public boolean isKingInCheck(Color kingColor) {
        Position kingPosition = getKingPosition(kingColor);
        ArrayList<Figure> enemyFigures = figures.values().stream()
                                        .filter(figure -> figure.getColor() != kingColor)
                                        .collect(Collectors.toCollection(ArrayList::new));

        for (Figure enemyFigure : enemyFigures) {
            List<Position> possibleMoves = getPossibleMoves(enemyFigure);
            if (possibleMoves.contains(kingPosition)) {
                return true; // Król jest w szachu
            }
        }

        return false;
    }

    public List<Position> getPossibleMoves(Figure figure) {
        return figure.getPossibleMoves(figures);
    }

    public Position getKingPosition(Color kingColor) {
        for (Map.Entry<Position, Figure> entry : figures.entrySet()) {
            if (entry.getValue() instanceof King && entry.getValue().getColor() == kingColor) {
                return entry.getKey();
            }
        }
        return null; // Powinno rzucić wyjątek, jeśli król nie został znaleziony
    }

    public Optional<Position> getKingCheckPosition(Figure figure) {
        final Color diffColor = (figure.getColor() == Color.WHITE) ? Color.BLACK : Color.WHITE;
        final Position kingPosition = isKingInCheck(diffColor) ? getKingPosition(diffColor) : null;

        return Optional.ofNullable(kingPosition);
    }

    public boolean checkGameOver(Figure figure, Position position) {
        if (checkForCheckmate(currentTurn, figure, position)) {
            gameOver = true;
            System.out.println((currentTurn == Color.WHITE ? "Białe" : "Czarne") + " jest w szach-mat! Koniec gry.");
            return true;
        }

        return false;
    }

    public void toggleTurn() {
        currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }
}
