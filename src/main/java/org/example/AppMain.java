package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.view.LogIn;

import java.io.IOException;

public class AppMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.initStyle(StageStyle.UNDECORATED);
        LogIn.show();
    }

    public static void main(String[] args) {
        launch();
    }}
