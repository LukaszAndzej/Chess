package controller;

import lib.figures.King;
import lib.figures.Pawn;
import lib.interfaces.Figure;
import lib.logic.FigureFactory;
import lib.logic.Position;
import model.FigureManager;
import model.GameStateManager;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.paint.Color;


public class BoardController {
    private static final int TILE_SIZE = 100;
    private static final int WIDTH = 8;
    private static final int HEIGHT = 8;

    private GridPane gridPane;
    private GameStateManager gameStateManager;
    private FigureManager figureManager;


    public BoardController() {
        this.gridPane = new GridPane();
        this.gameStateManager = new GameStateManager(gridPane);
        this.figureManager = new FigureManager(gameStateManager, gridPane);
        initializeBoard();
    }

    private void initializeBoard() {
        // Initialize grid cells
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Color rectColor = ((x + y) % 2 == 0) ? Color.BEIGE : Color.SADDLEBROWN;
                Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE, rectColor);
                Position position = new Position(x, y);
                rect.setFill(rectColor);
                gridPane.add(rect, x, y);
                setupDragNDropForRectangle(rect, position);
            }
        }
        setupFigures(Color.BLACK);
        setupFigures(Color.WHITE);
    }

    public void setupFigures(final Color color) {
        int backRow = (color == Color.WHITE) ? 0 : 7;
        int pawnRow = (color == Color.WHITE) ? 1 : 6;
        Map<Position, Figure> figures = new HashMap<>();
        final Map<String, List<Integer>> figurePositions = Map.of(
            "Rook", List.of(0, 7),
            "Knight", List.of(1, 6),
            "Bishop", List.of(2, 5),
            "Queen", List.of(3),
            "King", List.of(4),
            "Pawn", IntStream.range(0, WIDTH).boxed().collect(Collectors.toList())
        );

        for (Map.Entry<String, List<Integer>> figure: figurePositions.entrySet()) {
            for (Integer raw: figure.getValue()) {
                String figureName = figure.getKey();
                Position position = new Position(raw, (figureName == "Pawn") ? pawnRow : backRow);
                Figure newFigure = FigureFactory.createFigure(figureName, position, color);
                ImageView imageView = figureManager.addFigure(newFigure, position);

                figures.put(position, newFigure);
                figureManager.setupDragNDropImageView(imageView, position);
            }
        }

        gameStateManager.initializeGame(figures);
    }

    public void addFigure(ImageView imageView, Position position, Color color) {
        gridPane.add(imageView, position.getX(), position.getY());
        imageView.setMouseTransparent(color != gameStateManager.getCurrentTurn()); // Aktywuj tylko figury, które mają turę
        imageView.toFront();
    }

    public GridPane getGridPane() {
        return this.gridPane;
    }

    private void setupDragNDropForRectangle(Rectangle rect, Position position) {
        rect.setOnDragOver(event -> {
            if (event.getDragboard().hasContent(DataFormat.IMAGE)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        rect.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if (!db.hasImage()) return;
    
            ImageView source = (ImageView) event.getGestureSource();
            Figure figure = (Figure) source.getUserData();
            Position startPosition = figureManager.getPositionFromImageView(source);  // Pobierz pozycję początkową
            Position endPosition = position;

            if (gameStateManager.isMoveAllowed(figure, endPosition) &&
                gameStateManager.executeMove(figure, startPosition, endPosition)) {
                updateFigureOnBoard(source, endPosition); // Aktualizacja pozycji w GUI
                checkPawnPromotion(figure, endPosition); // Sprawdzenie promocji pionka

                // Aktualizacja pozycji wieży przy roszadzie
                if (figure instanceof King && Math.abs(startPosition.getX() - endPosition.getX()) == 2) {
                    int posStartX = (endPosition.getX() > startPosition.getX()) ? 7 : 0; // short : long length
                    int posEndX = (endPosition.getX() > startPosition.getX()) ? 1 : -1;

                    Position rookStartPosition = new Position(posStartX, startPosition.getY());
                    Position rookEndPosition = new Position(startPosition.getX() + posEndX, startPosition.getY());
                    ImageView rookImageView = (ImageView) FigureManager.getNode(gridPane, rookStartPosition.getY(), rookStartPosition.getX());
                    Figure rookFigure = (Figure) rookImageView.getUserData();

                    rookFigure.setPosition(rookEndPosition);
                    gameStateManager.executeMove(rookFigure, rookStartPosition, rookEndPosition);
                    updateFigureOnBoard(rookImageView, rookEndPosition);
                    updateFigureInteractivity();
                }

                event.setDropCompleted(true);
            }

            event.consume();
            figureManager.clearHighlights();

            if (figure.getColor() == gameStateManager.getCurrentTurn()) {
                gameStateManager.toggleTurn();
            }

            if(gameStateManager.checkGameOver(figure, position)) {
                Image gameOverImage = new Image("file:pic/game/gameOver.jpg"); // Zakładamy, że obrazek znajduje się w katalogu głównym projektu
                ImageView imageView = new ImageView(gameOverImage);
                imageView.setFitWidth(400); // Ustaw szerokość obrazka
                imageView.setFitHeight(200); // Ustaw wysokość obrazka

                // Pozycjonowanie obrazka w centrum gridPane
                gridPane.add(imageView, 2, 0, 8, 8); // Dodanie obrazka do gridPane, zakładając że ma 8 kolumn i 8 wierszy
                GridPane.setColumnSpan(imageView, GridPane.REMAINING);
                GridPane.setRowSpan(imageView, GridPane.REMAINING);
            }
        });
    }
    
    private void updateFigureOnBoard(ImageView imageView, Position position) {
        gridPane.getChildren().remove(imageView);
        gridPane.add(imageView, position.getX(), position.getY());
        imageView.setLayoutX(0);
        imageView.setLayoutY(0);
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

    private void promotePawn(Figure pawn, Position position) {
        Stage promotionStage = new Stage();
        promotionStage.initModality(Modality.APPLICATION_MODAL);
        promotionStage.setTitle("Promote Pawn");

        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: beige; -fx-padding: 10;");

        String[] pieceNames = {"Queen", "Rook", "Bishop", "Knight"};
        for (String pieceName : pieceNames) {
            Figure pieceFigure = FigureFactory.createFigure(pieceName, position, pawn.getColor());
            Button button = new Button();

            button.setGraphic(pieceFigure.getImageView());
            button.setOnAction(e -> {
                // Usuwanie pionka z planszy
                ImageView pawnImageView = (ImageView) FigureManager.getNode(gridPane, pawn.getPosition().getY(), pawn.getPosition().getX());
                gridPane.getChildren().remove(pawnImageView);

                // Dodawanie nowej figury na planszę
                Figure newFigure = FigureFactory.createFigure(pieceName, position, pawn.getColor());
                ImageView newImageView = figureManager.addFigure(newFigure, position);
                figureManager.setupDragNDropImageView(newImageView, position);

                updateFigureOnBoard(newImageView, position);
                promotionStage.close();
            });

            hbox.getChildren().add(button);
        }

        Scene scene = new Scene(hbox);
        promotionStage.setScene(scene);
        promotionStage.showAndWait();
    }

    private void checkPawnPromotion(Figure figure, Position position) {
        if (figure instanceof Pawn) {
            if ((figure.getColor() == Color.WHITE && position.getY() == 7) ||
                (figure.getColor() == Color.BLACK && position.getY() == 0)) {
                promotePawn(figure, position);
            }
        }
    }

}
