package org.example.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.AppMain;

import java.io.IOException;

public class AddUser {
    private static Stage stageAddUser = new Stage();
    public static void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("AddUser.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stageAddUser.setTitle("ServiStat");
        stageAddUser.setScene(scene);
        stageAddUser.setResizable(false);
        stageAddUser.show();
    }

    public static Stage getStageAddUser() {
        return stageAddUser;
    }
}
