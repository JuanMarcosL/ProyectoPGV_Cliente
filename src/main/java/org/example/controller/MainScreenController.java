package org.example.controller;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.AppMain;
import org.example.connection.TCPClient;
import eu.hansolo.medusa.Gauge;

import java.io.IOException;

public class MainScreenController {

     private static Stage stageMainScreen;
    @FXML
    public Button addServerButton;
    @FXML
    public Button botonCerrar;

    @FXML
    private Gauge gaugeRAM;

    @FXML
    private BorderPane borderPaneServers;

    @FXML
    private VBox vBoxServers;

    public void init(){
        gaugeRAM.setTitle("RAM");
        gaugeRAM.setUnit("%");

    }
    public static void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(AppMain.class.getResource("..\\..\\CSS\\styles.css").toExternalForm()); // Carga la hoja de estilo
        stageMainScreen.setTitle("ServiStat");
        stageMainScreen.setScene(scene);
        stageMainScreen.setResizable(false);

        stageMainScreen.show();

//        stageMainScreen.setOnCloseRequest(event -> {
//            Platform.exit();
//            System.exit(0);
//        });

    }

    public static Stage getStageMainScreen() {
        return stageMainScreen;
    }

    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }


    public void initialize() {
        gaugeRAM.setUnit("%");
        gaugeRAM.setTitle("RAM");
        gaugeRAM.setThreshold(65);
        gaugeRAM.setThresholdColor(Gauge.BRIGHT_COLOR);
        gaugeRAM.setThresholdVisible(true);

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
            double ramUsage = Double.parseDouble(messageFromTCPClient);

            Platform.runLater(() -> {
                gaugeRAM.setValue(ramUsage);
            });

        }
    }

    public void addServer(ActionEvent actionEvent) {
        showDialogAddServer(actionEvent);
    }

    public void showDialogAddServer(ActionEvent actionEvent) {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Añadir servidor");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField alias = addFieldToGrid(grid, "Alias:", 0);
        TextField ip = addFieldToGrid(grid, "Dirección IP:", 1);
        TextField puerto = addFieldToGrid(grid, "Puerto:", 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Añadir", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButtonType) {
                return new String[]{alias.getText(), ip.getText(), puerto.getText()};
            }
            return null;
        });

        String[] result = dialog.showAndWait().orElse(null);
        if (result != null) {
            new Thread(() -> {
                TCPClient tcpClient = new TCPClient(result[0], result[1], Integer.parseInt(result[2]));
            }).start();

            vBoxServers.setStyle("-fx-padding: 10;");
            // Crea un nuevo botón y lo agrega al StackPane
            Button serverButton = new Button(result[0]);
            serverButton.setMaxWidth(Double.MAX_VALUE);
            serverButton.setPrefHeight(50);
            serverButton.getStyleClass().add("servidores"); // Agrega la clase al botón



            Platform.runLater(() -> {
                vBoxServers.setSpacing(10);
                vBoxServers.getChildren().add(serverButton);
            });

    /*        HOVER SOBRE LOS BOTONES DE LOS SERVIDORES
            serverButton.setOnMouseEntered(event -> {
                KeyValue keyValue1 = new KeyValue(serverButton.styleProperty(), "-fx-background-color: #FFC125;");
                KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0.2), keyValue1);
                Timeline timeline1 = new Timeline(keyFrame1);
                timeline1.play();
            });

            serverButton.setOnMouseExited(event -> {
                KeyValue keyValue2 = new KeyValue(serverButton.styleProperty(), "-fx-background-color: #FFFFFF;");
                KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(0.2), keyValue2);
                Timeline timeline2 = new Timeline(keyFrame2);
                timeline2.play();
            });*/
        }
    }

    private TextField addFieldToGrid(GridPane grid, String labelText, int row) {
        TextField textField = new TextField();
        grid.add(new Label(labelText), 0, row);
        grid.add(textField, 1, row);
        return textField;
    }
}