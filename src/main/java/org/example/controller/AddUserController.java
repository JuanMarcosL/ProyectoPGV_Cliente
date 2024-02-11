package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.models.User;

import java.io.IOException;

/**
 * Esta clase controla la funcionalidad de la pantalla de registro de usuario.
 */
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

    /**
     * Este método se ejecuta cuando el usuario hace clic en el botón de registro.
     * Realiza la validación de los campos de entrada y registra al usuario si la validación es exitosa.
     *
     * @param actionEvent El evento de acción.
     * @throws IOException Si ocurre un error al cargar la interfaz de usuario.
     */
    public void aceptarRegistro(ActionEvent actionEvent) throws IOException {

        String usuario = textFieldUsuarioRegistro.getText();
        String correo = textFieldCorreoRegistro.getText();
        String confirmacionCorreo = textFieldConfirmacionCorreo.getText();
        String clave = passwordFieldClaveRegistro.getText();

        if (usuario.isEmpty() || correo.isEmpty() || confirmacionCorreo.isEmpty() || clave.isEmpty() || !checkboxCondiciones.isSelected()) {
            mostrarMensajeError("Campos vacíos", "Debes rellenar todos los campos y aceptar las condiciones de uso");
        } else if (User.userExists(usuario)) {
            mostrarMensajeError("Error", "El nombre de usuario ya está en uso ");
        } else if (!comprobarFormatoCorreo(correo) || !comprobarFormatoCorreo(confirmacionCorreo)) {
            mostrarMensajeError("Error", "El correo introducido no tiene un formato válido");
        } else if (User.emailExists(correo)) {
            mostrarMensajeError("Error", "El correo ya está registrado");
        } else if (!correo.equals(confirmacionCorreo)) {
            mostrarMensajeError("Error", "Los correos no coinciden");
        } else {
            User.addUser(new User(usuario, clave, correo));

            mostrarMensajeInformacion("Registro exitoso", "El usuario ha sido registrado exitosamente");

            ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
            LogInController.show();
        }
    }

    /**
     * Este método comprueba si el correo electrónico proporcionado tiene un formato válido.
     *
     * @param correo El correo electrónico a comprobar.
     * @return Verdadero si el correo electrónico tiene un formato válido, falso en caso contrario.
     */
    private boolean comprobarFormatoCorreo(String correo) {

        String Pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        return correo.matches(Pattern);
    }

    /**
     * Este método muestra los términos y condiciones cuando el usuario hace clic en el enlace de términos y condiciones.
     *
     * @param mouseEvent El evento del ratón.
     */
    public void mostrarCondiciones(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Términos y condiciones");
        alert.setHeaderText(null);
        alert.setContentText("Hola, somos los términos y condiciones");
        alert.showAndWait();

    }

    /**
     * Este método muestra un mensaje de error con el título y el contenido proporcionados.
     *
     * @param Titulo El título del mensaje.
     * @param s      El contenido del mensaje.
     */
    private void mostrarMensajeError(String Titulo, String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Titulo);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    /**
     * Este método muestra un mensaje de confirmación con el título y el contenido proporcionados.
     *
     * @param cancelar El título del mensaje.
     * @param s        El contenido del mensaje.
     * @return Verdadero si el usuario hace clic en el botón de aceptar, falso en caso contrario.
     */
    private boolean mostrarMensajeConfirmacion(String cancelar, String s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(cancelar);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();

        return alert.getResult().getText().equals("Aceptar");
    }

    /**
     * Este método muestra un mensaje de información con el título y el contenido proporcionados.
     *
     * @param titulo El título del mensaje.
     * @param s      El contenido del mensaje.
     */
    private void mostrarMensajeInformacion(String titulo, String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    /**
     * Este método se ejecuta cuando el usuario hace clic en el botón de cancelar.
     * Muestra un mensaje de confirmación y cierra la pantalla de registro si el usuario confirma la cancelación.
     *
     * @param actionEvent El evento de acción.
     */
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

    /**
     * Este método se ejecuta cuando el usuario hace clic en el botón de atrás.
     * Cierra la pantalla de registro y muestra la pantalla de inicio de sesión.
     *
     * @param mouseEvent El evento del ratón.
     * @throws IOException Si ocurre un error al cargar la interfaz de usuario.
     */
    public void irAtras(MouseEvent mouseEvent) throws IOException {
        ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).close();
        LogInController.show();
    }


}
