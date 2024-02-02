package org.example.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.AppMain;
import org.example.connection.TCPClient;
import eu.hansolo.medusa.Gauge;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import java.io.IOException;

public class MainScreenController {

    private static double usedPercentage;

    private static Stage stageMainScreen;
    @FXML
    public Button addServerButton;

    @FXML
    private Gauge lineRAM;
    @FXML
    private Gauge gaugeRAM;


//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        this.stageMainScreen = primaryStage;
//        show();
//    }

    public static void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stageMainScreen.setTitle("ServiStat");
        stageMainScreen.setScene(scene);
        stageMainScreen.setResizable(false);
        stageMainScreen.show();
    }

    public static Stage getStageMainScreen() {
        return stageMainScreen;
    }

    public void closeApp(ActionEvent actionEvent) {
    }



    public void initialize() {

        // new Thread(this::monitorizeRAM).start();

        gaugeRAM.setValue(usedPercentage);
        gaugeRAM.setUnit("%");
        gaugeRAM.setDecimals(1);
        gaugeRAM.setAnimated(true);
        gaugeRAM.setAnimationDuration(1000);
        gaugeRAM.setBarColor(Gauge.DARK_COLOR);
        //gaugeRAM.setNeedleColor(Gauge.DARK_COLOR);
        gaugeRAM.setThresholdColor(Gauge.DARK_COLOR);
        //gaugeRAM.setThreshold(80);
        gaugeRAM.setThresholdVisible(true);

        lineRAM.setValue(usedPercentage);
        lineRAM.setUnit("%");
        lineRAM.setDecimals(2);
        lineRAM.setAnimated(true);
        lineRAM.setAnimationDuration(1000);
        lineRAM.setBarColor(Gauge.DARK_COLOR);
        lineRAM.setThreshold(80);
        lineRAM.setThresholdVisible(true);

        new Thread(() -> {
            while (true) {
                updateGauges();

                try {
                    Thread.sleep(1000); // Wait 1 second before the next update
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

public void updateGauges() {
    String messageFromTCPClient = TCPClient.getLastMessage();
    if (!messageFromTCPClient.isEmpty()) {
        System.out.println("Mensaje recibido del servidor: " + messageFromTCPClient);
        double ramUsage = Double.parseDouble(messageFromTCPClient);

        // Actualiza los medidores en el hilo de la interfaz de usuario
        Platform.runLater(() -> {
            gaugeRAM.setValue(ramUsage);
            lineRAM.setValue(ramUsage);
        });
    } else {
        System.out.println("No se ha recibido mensaje del servidor.");
    }
}

public void showDialogAddServer(ActionEvent actionEvent) {
    Dialog<String[]> dialog = new Dialog<>();
    dialog.setTitle("Añadir servidor");

// Configura el contenido del diálogo
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField alias = new TextField();
    TextField ip = new TextField();
    TextField puerto = new TextField();

    grid.add(new Label("Alias:"), 0, 0);
    grid.add(alias, 1, 0);
    grid.add(new Label("Dirección IP:"), 0, 1);
    grid.add(ip, 1, 1);
    grid.add(new Label("Puerto:"), 0, 2);
    grid.add(puerto, 1, 2);

    dialog.getDialogPane().setContent(grid);

// Añade los botones de "Añadir" y "Cancelar" al diálogo
    ButtonType addButtonType = new ButtonType("Añadir", ButtonType.OK.getButtonData());
    dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

// Configura el resultado del diálogo para que sea el texto introducido por el usuario
    dialog.setResultConverter(buttonType -> {
        if (buttonType == addButtonType) {
            return new String[]{alias.getText(), ip.getText(), puerto.getText()};
        }
        return null;
    });

// Muestra el diálogo y obtén el resultado
    String[] result = dialog.showAndWait().orElse(null);
    if (result != null) {
        System.out.println("Alias: " + result[0]);
        System.out.println("Dirección IP: " + result[1]);
        System.out.println("Puerto: " + result[2]);
    }
}

    public void addServer(ActionEvent actionEvent) {
        showDialogAddServer(actionEvent);
    }
}