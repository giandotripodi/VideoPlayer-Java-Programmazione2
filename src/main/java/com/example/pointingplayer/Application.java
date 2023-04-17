package com.example.pointingplayer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;


public class Application extends javafx.application.Application {
    static String filename;

    @Override
    public void start(Stage stage) throws Exception {
        filename = getParameters().getUnnamed().get(0);
//        filename = "motherT0.mp4";

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("player-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 800);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setTitle("Pointing Player");
        stage.getIcons().add(new Image("C://Users//giand//IdeaProjects//PointingPlayer//src//main//resources//icons/icon.png"));

        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent doubleClicked) {
                if(doubleClicked.getClickCount() == 2) {
                    stage.setFullScreen(true);
                }
            }
        });

        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            Controller ctrl = fxmlLoader.getController();
            ctrl.exitVideo(new ActionEvent());
            event.consume();
        });
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}


