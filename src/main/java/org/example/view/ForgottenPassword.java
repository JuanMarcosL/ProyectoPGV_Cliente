package org.example.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.AppMain;

import java.io.IOException;

public class ForgottenPassword {
    private static Stage stageForgottenPassword = new Stage();
    public static void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("ForgottenPassword.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stageForgottenPassword.setTitle("ServiStat");
        stageForgottenPassword.setScene(scene);
        stageForgottenPassword.setResizable(false);
        stageForgottenPassword.show();
    }

    public static Stage getStageForgottenPassword() {
        return stageForgottenPassword;
    }
}
