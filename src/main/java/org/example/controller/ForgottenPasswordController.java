package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.models.User;

import java.io.IOException;
import java.util.List;

import static org.example.models.User.getUsers;

/**
 * Esta clase controla la funcionalidad de la pantalla de recuperación de contraseña.
 */
public class ForgottenPasswordController {
    @FXML
    public TextField textFieldCorreoRecuperacion;
    @FXML
    public Button botonAceptarRecuperacion;

    private List<User> users = User.getUsers();

    /**
     * Este método se ejecuta cuando el usuario hace clic en el botón de cancelar.
     * Muestra un mensaje de confirmación y cierra la pantalla de recuperación de contraseña si el usuario confirma la cancelación.
     * @param actionEvent El evento de acción.
     */
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

    /**
     * Este método se ejecuta cuando el usuario hace clic en el botón de aceptar.
     * Realiza la validación del correo electrónico y envía un correo de recuperación si la validación es exitosa.
     * @param actionEvent El evento de acción.
     */
    public void aceptarRecuperacion(ActionEvent actionEvent) {
        String correo = textFieldCorreoRecuperacion.getText();
        if (!comprobarFormatoCorreo(correo)) {
            mostrarMensajeError("Error", "El correo introducido no tiene un formato válido");

        } else {
            for (User user : users) {
                if (user.getEmail().equals(correo)) {
                    mostrarMensajeInformacion("Recuperación de contraseña", "Se ha enviado un correo a " + correo + " con las instrucciones para recuperar la contraseña");
                    return;
                }
            }
            mostrarMensajeError("Error", "No existe ningún usuario registrado con el correo " + correo);
        }
    }

    /**
     * Este método muestra un mensaje de información con el título y el contenido proporcionados.
     * @param titulo El título del mensaje.
     * @param s El contenido del mensaje.
     */
    private void mostrarMensajeInformacion(String titulo, String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    /**
     * Este método se ejecuta cuando el usuario hace clic en el botón de atrás.
     * Cierra la pantalla de recuperación de contraseña y muestra la pantalla de inicio de sesión.
     * @param mouseEvent El evento del ratón.
     * @throws IOException Si ocurre un error al cargar la interfaz de usuario.
     */
    public void irAtras(MouseEvent mouseEvent) throws IOException {
        ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).close();
        LogInController.show();
    }

    /**
     * Este método comprueba si el correo electrónico proporcionado tiene un formato válido.
     * @param correo El correo electrónico a comprobar.
     * @return Verdadero si el correo electrónico tiene un formato válido, falso en caso contrario.
     */
    private boolean comprobarFormatoCorreo(String correo) {

        String Pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return correo.matches(Pattern);
    }

    /**
     * Este método muestra un mensaje de confirmación con el título y el contenido proporcionados.
     * @param cancelar El título del mensaje.
     * @param s El contenido del mensaje.
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
     * Este método muestra un mensaje de error con el título y el contenido proporcionados.
     * @param error El título del mensaje.
     * @param s El contenido del mensaje.
     */
    private void mostrarMensajeError(String error, String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(error);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }
}
