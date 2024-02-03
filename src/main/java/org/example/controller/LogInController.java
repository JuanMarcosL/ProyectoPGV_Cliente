package org.example.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.AppMain;
import org.example.connection.TCPClient;

import java.io.IOException;

public class LogInController {

    @FXML
    public Button botonCerrar;
    @FXML
    public Button botonClaveOlvidada;
    @FXML
    public Button iniciarSesion;

    public void recuperarClave(ActionEvent actionEvent) {
    }


//    botonCerrar.setOnAction(this::closeApp);

    public void iniciarSesion(ActionEvent actionEvent) {

        try {
            // Cargar el archivo FXML de MainScreen
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/MainScreen.fxml"));
            // Crear una nueva escena
            Scene scene = new Scene(root);
            // Obtener el escenario actual y establecer la nueva escena
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.centerOnScreen();

            // Cerrar el Stage anterior
            ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();

            stage.show();


            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public static void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
