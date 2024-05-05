package lib.interfaces;

import lib.logic.Position;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

public abstract class Figure implements MoveLogic {
    public static final int TILE_SIZE = 100;
    protected Image image;
    protected Position position;
    protected Color color;
    protected String name;
    protected boolean moved = false;

    public Figure(String imagePath, Color color, Position position) {
        this.image = new Image(imagePath);
        this.color = color;
        this.position = position;
    }

    public ImageView getImageView() {
        ImageView imageView = new ImageView(image);
        imageView.setUserData(this);
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        return imageView;
    }

    public void setupImageViewEvents(ImageView imageView) {
        imageView.setOnDragDetected(event -> {
            Dragboard db = imageView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(imageView.getImage());
            db.setContent(content);
            imageView.startFullDrag();
            event.consume();
        });

        imageView.setOnDragDone(event -> {
            imageView.setMouseTransparent(false);
            event.consume();
        });
    }

    @Override
    public void move(int newX, int newY) {
        this.position.setX(newX);
        this.position.setY(newY);
        this.moved = true;
    }

    public Position getPosition() { return this.position;}

    public void setPosition(Position position) { this.position = position;}

    public String getName() { return this.name;}

    public Color getColor() { return this.color;}

    public boolean hasMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public void setX(int x) {this.position.setX(x);}

    public void setY(int y) {this.position.setY(y);}

    public int getX() { return this.position.getX();}

    public int getY() { return this.position.getY();}

}
