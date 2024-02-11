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
import org.example.models.User;

import java.io.IOException;
import java.util.List;

/**
 * Esta clase controla la funcionalidad de la pantalla de inicio de sesión.
 */
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

    private List<User> users = User.getUsers();

    private static User usuarioActual;


    /**
     * Este método se ejecuta cuando el usuario hace clic en el botón de inicio de sesión.
     * Realiza la validación de los campos de entrada y inicia la sesión si la validación es exitosa.
     *
     * @param actionEvent El evento de acción.
     */
    public void iniciarSesion(ActionEvent actionEvent) {

        if (camposValidos()) {
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
        }
    }

    /**
     * Este método comprueba si los campos de entrada son válidos.
     *
     * @return Verdadero si los campos son válidos, falso en caso contrario.
     */
    private boolean camposValidos() {
        if (textFieldUsuario.getText().isEmpty() || passwordFieldClave.getText().isEmpty()) {
            mostrarMensajeError("Campos vacíos", "No puede haber campos vacíos");
            return false;

        } else {
            for (User user : users) {
                System.out.println(user.getUsername() + " " + user.getPassword());
                if (user.getUsername().equalsIgnoreCase(textFieldUsuario.getText()) && user.getPassword().equals(passwordFieldClave.getText())) {
                    usuarioActual = user;
                    return true;
                }
            }

            if (!textFieldUsuario.getText().equals("admin") || !passwordFieldClave.getText().equals("admin")) {
                mostrarMensajeError("Error", "Usuario o contraseña incorrectos");
                return false;
            }
        }
        return true;
    }

    /**
     * Este método muestra la pantalla de inicio de sesión.
     *
     * @throws IOException Si ocurre un error al cargar la interfaz de usuario.
     */
    public static void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Este método se ejecuta cuando el usuario hace clic en el botón de registro.
     * Abre la pantalla de registro.
     *
     * @param actionEvent El evento de acción.
     */
    public void iniciarRegistro(ActionEvent actionEvent) {
        abrirNuevaVentana("/org/example/AddUser.fxml", actionEvent);
    }

    /**
     * Este método se ejecuta cuando el usuario hace clic en el botón de recuperación de contraseña.
     * Abre la pantalla de recuperación de contraseña.
     *
     * @param actionEvent El evento de acción.
     */
    public void recuperarClave(ActionEvent actionEvent) {
        abrirNuevaVentana("/org/example/ForgottenPassword.fxml", actionEvent);
    }

    /**
     * Este método abre una nueva ventana con la interfaz de usuario especificada.
     *
     * @param rutaFXML    La ruta al archivo FXML de la interfaz de usuario.
     * @param actionEvent El evento de acción.
     */
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

    /**
     * Este método muestra un mensaje de error con la cabecera y el mensaje proporcionados.
     *
     * @param Cabecera La cabecera del mensaje.
     * @param mensaje  El mensaje.
     */
    private void mostrarMensajeError(String Cabecera, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(Cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * método que devuelve el usuario actual
     *
     * @return el usuario actual
     */
    public static User getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * método que establece el usuario actual
     *
     * @param usuario el usuario actual
     */
    public static void setUsuarioActual(User usuario) {
        usuarioActual = usuario;
    }
}
