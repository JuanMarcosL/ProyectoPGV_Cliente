package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class ForgottenPasswordController {
    @FXML
    public TextField textFieldCorreoRecuperacion;
    @FXML
    public Button botonAceptarRecuperacion;


    public void cancelar(ActionEvent actionEvent) {

        if (mostrarMensajeConfirmacion("Cancelar", "¿Estás seguro de que quieres cancelar la recuperación de la contraseña?")) {

            try {
                ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
                LogInController.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void aceptarRecuperacion(ActionEvent actionEvent) {
        String correo = textFieldCorreoRecuperacion.getText();
        if (!comprobarFormatoCorreo(correo)) {
            mostrarMensajeError("Error", "El correo introducido no tiene un formato válido");

        } else {
            /*TODO comprobar en la base de datos si existe el correo y mostrar mensaje de confirmacion*/
            mostrarMensajeInformacion("Recuperación de contraseña", "Se ha enviado un correo a " + correo + " con las instrucciones para recuperar la contraseña");
        }
    }

    private void mostrarMensajeInformacion(String titulo, String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    public void irAtras(MouseEvent mouseEvent) throws IOException {
        ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).close();
        LogInController.show();
    }

    private boolean comprobarFormatoCorreo(String correo) {

        String Pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return correo.matches(Pattern);
    }

    private boolean mostrarMensajeConfirmacion(String cancelar, String s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(cancelar);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();

        return alert.getResult().getText().equals("Aceptar");
    }

    private void mostrarMensajeError(String error, String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(error);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }
}
