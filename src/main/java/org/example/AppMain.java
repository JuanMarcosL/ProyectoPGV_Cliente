package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.view.LogIn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Esta es la clase principal de la aplicación.
 * Inicia la interfaz de usuario y maneja la creación del archivo de reporte.
 */
public class AppMain extends Application {

    /**
     * Este método se ejecuta al inicio de la aplicación y muestra la interfaz de inicio de sesión.
     * @param stage El escenario principal de la aplicación.
     * @throws IOException Si ocurre un error al cargar la interfaz de usuario.
     */
    @Override
    public void start(Stage stage) throws IOException {
        LogIn.show();
    }

    /**
     * El método main es el punto de entrada de la aplicación.
     * Crea o recrea el archivo de reporte y luego inicia la interfaz de usuario.
     * @param args Los argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        String nombreArchivo = "src/main/resources/CSV/reporte.csv";
        File archivo = new File(nombreArchivo);

        try {
            // Si el archivo existe, eliminarlo
            if (archivo.exists()) {
                archivo.delete();
            }

            // Crear el archivo nuevamente
            archivo.createNewFile();

            // Ahora que el archivo existe, abrirlo para escribir
            try (FileWriter archivoCSV = new FileWriter(nombreArchivo, true)) {
                // Como el archivo es nuevo, escribir la cabecera
                archivoCSV.append("alias,alerta,valor\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        launch();
    }
}