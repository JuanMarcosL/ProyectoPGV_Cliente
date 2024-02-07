package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.view.LogIn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AppMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {


        LogIn.show();
    }

    public static void main(String[] args) {


        String nombreArchivo = "src/main/resources/CSV/reporte.csv";
        File archivo = new File(nombreArchivo);

        try {
            // Crear el archivo si no existe
            if (!archivo.exists()) {
                archivo.createNewFile();
            }

            // Ahora que el archivo existe, abrirlo para escribir
            try (FileWriter archivoCSV = new FileWriter(nombreArchivo, true)) {
                // Si el archivo está vacío, escribir la cabecera
                if (archivo.length() == 0) {
                    archivoCSV.append("alias,alerta,valor\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        launch();
    }
}
