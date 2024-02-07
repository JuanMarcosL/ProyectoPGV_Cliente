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
