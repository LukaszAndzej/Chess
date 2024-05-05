package src;

import controller.BoardController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Chess extends Application {

    @Override
    public void start(Stage primaryStage) {
        BoardController board = new BoardController();
        Scene scene = new Scene(board.getGridPane());

        primaryStage.setTitle("Chess");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
