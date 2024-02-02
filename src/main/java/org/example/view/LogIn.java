package org.example.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.AppMain;

import java.io.IOException;

public class LogIn {
    private static Stage stageLogIn = new Stage();
//    public static void show() throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("LogIn.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        stageLogIn.setTitle("ServiStat");
//        stageLogIn.setScene(scene);
//        stageLogIn.setResizable(false);
//        stageLogIn.show();
//    }

    public static void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(AppMain.class.getResource("LogIn.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("ServiStat");
        stage.setResizable(false);
        stage.show();
    }


    public static Stage getStageLogIn() {
        return stageLogIn;
    }


}