package org.example.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.connection.TCPClient;

import java.io.IOException;

public class LogInController {

    public void recuperarClave(ActionEvent actionEvent) {
    }

    public void iniciarSesion(ActionEvent actionEvent) {

        try {
            // Cargar el archivo FXML de MainScreen
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/MainScreen.fxml"));
            // Crear una nueva escena
            Scene scene = new Scene(root);
            // Obtener el escenario actual y establecer la nueva escena
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("ServiStat");
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeApp(ActionEvent actionEvent) {
    }
}
