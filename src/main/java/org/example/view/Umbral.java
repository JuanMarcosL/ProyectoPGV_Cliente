package org.example.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.AppMain;

import java.io.IOException;

public class Umbral {
    private static Stage stageUmbral = new Stage();
    public static void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("SetUmbrals.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stageUmbral.setTitle("ServiStat");
        stageUmbral.setScene(scene);
        stageUmbral.setResizable(false);
        stageUmbral.show();
    }

    public static Stage getStageAddUser() {
        return stageUmbral;
    }
}
