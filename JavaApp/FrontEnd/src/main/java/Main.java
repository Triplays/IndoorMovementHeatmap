package main.java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.java.Browser;

public class Main extends Application {

    int width = 750;
    int height = 500;
    private Scene scene;


    @Override
    public void start(Stage stage) {
        // create the scene
        stage.setTitle("Indoor Movement Heat Map Viewer");
        scene = new Scene(new Browser(), width, height, Color.web("#666970"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}




