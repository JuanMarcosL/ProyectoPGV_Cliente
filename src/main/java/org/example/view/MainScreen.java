package org.example.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.AppMain;

import java.io.IOException;

public class MainScreen {
    private static Stage stageMainScreen = new Stage();
    public static void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("MainScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stageMainScreen.setTitle("ServiStat");
        stageMainScreen.setScene(scene);
        stageMainScreen.setResizable(false);
        stageMainScreen.show();

    }

    public static Stage getStageMainScreen() {
        return stageMainScreen;
    }
}

