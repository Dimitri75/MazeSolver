package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("xml/sample.fxml"));
        primaryStage.setTitle("Romeo et Juliette");
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        Controller.showInstructions();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
