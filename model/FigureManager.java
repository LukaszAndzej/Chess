package model;

import lib.interfaces.Figure;
import lib.logic.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class FigureManager {
    private static final int TILE_SIZE = 100;

    private GridPane gridPane;
    private GameStateManager gameStateManager;
    private Color currentTurn = Color.WHITE;

    private Map<Position, Figure> figuresOld = new HashMap<>();
    private List<Figure> figures = new ArrayList<>();
    private List<Rectangle> activeHighlights = new ArrayList<>();

    public FigureManager(GameStateManager gameStateManager, GridPane gridPane) {
        this.gameStateManager = gameStateManager;
        this.gridPane = gridPane;
    }

    public ImageView addFigure(Figure figure, Position position) {
        ImageView imageView = figure.getImageView();
        figure.setupImageViewEvents(imageView);
        gridPane.add(imageView, figure.getPosition().getX(), figure.getPosition().getY());
        imageView.setMouseTransparent(figure.getColor() != currentTurn);
        figures.add(figure);
        figuresOld.put(figure.getPosition(), figure);

        return imageView;
    }

    public void setupDragNDropImageView(ImageView imageView, Position position) {
        imageView.setOnDragDetected(event -> {
            Dragboard db = imageView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(imageView.getImage());
            db.setContent(content);

            Figure figure = (Figure) imageView.getUserData();
            List<Position> possibleMoves = gameStateManager.getPossibleMoves(figure);

            highlightPossibleMoves(possibleMoves);

            event.consume();
        });
    
        imageView.setOnDragOver(event -> {
            if (event.getGestureSource() != imageView && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });
    
        imageView.setOnDragDone(event -> {
            updateFigureInteractivity();
            clearHighlights();

            final Color red = new Color(1, 0, 0, 0.3);
            final Figure figure = (Figure) imageView.getUserData();
            final Optional<Position> kingPosition = gameStateManager.getKingCheckPosition(figure);

            kingPosition.ifPresent(pos -> highlightPosition(pos, red));

            if (figure.getColor() == gameStateManager.getCurrentTurn()) {
                gameStateManager.toggleTurn();
            }

            event.consume();
        });
    }

    private void updateFigureInteractivity() {
        Map<Position, Figure> figures = new HashMap<>();
        for (Node node : gridPane.getChildren()) {
            if (node instanceof ImageView) {
                Figure figure = (Figure) ((ImageView) node).getUserData();
                if (figure == null) continue;

                node.setMouseTransparent(figure.getColor() != gameStateManager.getCurrentTurn());

                figures.put(figure.getPosition(), figure);
            }
        }
        gameStateManager.initializeGame(figures);
    }

    public void moveFigure(Position oldPosition, Position newPosition) {
        Figure figure = figuresOld.remove(oldPosition);
        if (figure != null) {
            figuresOld.put(newPosition, figure);
            figure.setPosition(newPosition);
        }
    }

    public Position getPositionOf(Figure figure) {
        return figuresOld.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(figure))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);  // Return null if figure is not found
    }

    public void removeFigure(Position position) {
        figuresOld.remove(position);
    }

    public Position getPositionFromImageView(ImageView imageView) {
        int x = GridPane.getColumnIndex(imageView);
        int y = GridPane.getRowIndex(imageView);
        return new Position(x, y);
    }

    public Figure getFigureAt(Position position) {
        return figuresOld.get(position);
    }

    private void highlightPossibleMoves(List<Position> possibleMoves) {
        clearHighlights(); // Usuwanie istniejących podświetleń
        final Color highLightAllowed = new Color(0, 1, 0, 0.3); // przezroczysty zielony
        for (Position pos : possibleMoves) {
            highlightPosition(pos, highLightAllowed);
        }
    }

    private void highlightPosition(Position position, Color color) {
        Rectangle highlightRect = new Rectangle(TILE_SIZE, TILE_SIZE);
        highlightRect.setFill(color);  // przezroczysty kolor
        highlightRect.setMouseTransparent(true); // Nie blokują myszy

        gridPane.add(highlightRect, position.getX(), position.getY());
        activeHighlights.add(highlightRect);
    }

    public void clearHighlights() {
        for (Rectangle rect : activeHighlights) {
            gridPane.getChildren().remove(rect);
        }
        activeHighlights.clear();
    }
    
    public static Node getNode(GridPane gridPane, final int row, final int column) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                if (node instanceof ImageView) {
                    return node; // Return ImageView if it exists at the position
                }
            }
        }
        // If no ImageView is found at the position, return any node (usually Rectangle)
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                return node;
            }
        }
        return null;
    }

}
