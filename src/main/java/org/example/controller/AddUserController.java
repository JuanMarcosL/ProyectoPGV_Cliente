package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class AddUserController {
    @FXML
    public CheckBox checkboxCondiciones;
    @FXML
    public TextField textFieldConfirmacionCorreo;
    @FXML
    public TextField textFieldCorreoRegistro;
    @FXML
    public PasswordField passwordFieldClaveRegistro;
    @FXML
    public TextField textFieldUsuarioRegistro;
    @FXML
    public Label labelCondiciones;

    public void aceptarRegistro(ActionEvent actionEvent) throws IOException {

        String usuario = textFieldUsuarioRegistro.getText();
        String correo = textFieldCorreoRegistro.getText();
        String confirmacionCorreo = textFieldConfirmacionCorreo.getText();
        String clave = passwordFieldClaveRegistro.getText();

        if (usuario.isEmpty() || correo.isEmpty() || confirmacionCorreo.isEmpty() || clave.isEmpty() || !checkboxCondiciones.isSelected()) {
            mostrarMensajeError("Campos vacíos", "Debes rellenar todos los campos y aceptar las condiciones de uso");
        } else if  (!comprobarFormatoCorreo(correo) || !comprobarFormatoCorreo(confirmacionCorreo)){
            mostrarMensajeError("Error", "El correo introducido no tiene un formato válido");
        } else if (!correo.equals(confirmacionCorreo)) {
            mostrarMensajeError("Error", "Los correos no coinciden");
        } else {
            /*TODO Realizar la inserción en la base de datos*/

            mostrarMensajeInformacion("Registro exitoso", "El usuario ha sido registrado exitosamente");

            ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
            LogInController.show();
        }
    }

    private boolean comprobarFormatoCorreo(String correo) {

        String Pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        return correo.matches(Pattern);
    }

    public void mostrarCondiciones(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Términos y condiciones");
        alert.setHeaderText(null);
        alert.setContentText("Hola, somos los términos y condiciones");
        alert.showAndWait();

    }

    private void mostrarMensajeError(String Titulo, String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Titulo);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }


    private boolean mostrarMensajeConfirmacion(String cancelar, String s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(cancelar);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();

        return alert.getResult().getText().equals("Aceptar");
    }

    private void mostrarMensajeInformacion(String titulo, String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    public void cancelar(ActionEvent actionEvent) {
        if (mostrarMensajeConfirmacion("Cancelar", "¿Estás seguro de que quieres cancelar el registro?")) {

            try {
                ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
                LogInController.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void irAtras(MouseEvent mouseEvent) throws IOException {
        ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).close();
        LogInController.show();
    }
}
