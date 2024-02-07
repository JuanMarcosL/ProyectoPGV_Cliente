package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.AppMain;
import java.io.IOException;

public class LogInController {

    @FXML
    public Button botonClaveOlvidada;
    @FXML
    public Button iniciarSesion;
    @FXML
    public Button botonRegistrarse;
    @FXML
    public PasswordField passwordFieldClave;
    @FXML
    public TextField textFieldUsuario;


    public void iniciarSesion(ActionEvent actionEvent) {

        if (camposVálidos()) {
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

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
           // mostrarMensajeError("Error","Usuario o contraseña incorrectos");
        }

    }

    private boolean camposVálidos() {
        if (textFieldUsuario.getText().isEmpty()){
            mostrarMensajeError("Campos vacíos","El campo usuario no puede estar vacío");

            return false;
        }

        if (passwordFieldClave.getText().isEmpty()) {
            mostrarMensajeError("Campos vacíos","El campo contraseña no puede estar vacío");
            return false;
        }
        return true;
    }

    public static void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void iniciarRegistro(ActionEvent actionEvent) {
        abrirNuevaVentana("/org/example/AddUser.fxml", actionEvent);
    }

    public void recuperarClave(ActionEvent actionEvent) {
        abrirNuevaVentana("/org/example/ForgottenPassword.fxml", actionEvent);
    }

    private void abrirNuevaVentana(String rutaFXML, ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.centerOnScreen();
            ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void mostrarMensajeError(String Cabecera, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(Cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
