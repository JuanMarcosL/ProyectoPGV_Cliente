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
    @FXML
    private Label welcomeText;

    private TCPClient tcpClient;

    public void initTcpClient() {
        tcpClient = new TCPClient();
    }
//    @FXML
//    protected void onHelloButtonClick() {
//        welcomeText.setText("Welcome to JavaFX Application!");
//    }

    //    @FXML
//    private void handleLoginButtonAction(ActionEvent event) {
//        // Aquí va tu código de inicio de sesión existente...
//
//        // Luego de validar el inicio de sesión, inicia el cliente TCP
//        tcpClient.startClient();
//    }
    public void recuperarClave(ActionEvent actionEvent) {
    }

    public void iniciarSesion(ActionEvent actionEvent) {
        /* TODO abrir mainScreen*/
        try {
            // Cargar el archivo FXML de MainScreen
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/MainScreen.fxml"));

            // Crear una nueva escena
            Scene scene = new Scene(root);

            // Obtener el escenario actual y establecer la nueva escena
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            // Iniciar el cliente TCP en un nuevo hilo
            new Thread(this::initTcpClient).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
local
    localhost
        6789
*/
    public void closeApp(ActionEvent actionEvent) {
    }
}
