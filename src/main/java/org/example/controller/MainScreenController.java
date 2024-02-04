package org.example.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.AppMain;
import org.example.connection.TCPClient;
import eu.hansolo.medusa.Gauge;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainScreenController {

    private static Stage stageMainScreen;
    @FXML
    public Button addServerButton;
    @FXML
    public Button botonCerrar;
    @FXML
    public Gauge gaugeCPU;
    @FXML
    public ChoiceBox comboBoxDisks;
    @FXML
    public Gauge gaugeDisk;
    @FXML
    public TextField textFieldDisksFormat;
    @FXML
    public TextField textFieldDiskCapacity;
    @FXML
    public LineChart chartRed;

    @FXML
    private Gauge gaugeRAM;

    @FXML
    private BorderPane borderPaneServers;

    @FXML
    private VBox vBoxServers;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void init() {
//        gaugeRAM.setAlert(true);
//        gaugeRAM.alert
//        gaugeRAM.setTitle("RAM");
//        gaugeRAM.setUnit("%");1

    }

    public static void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(AppMain.class.getResource("..\\..\\CSS\\styles.css").toExternalForm()); // Carga la hoja de estilo
        stageMainScreen.setTitle("ServiStat");
        stageMainScreen.setScene(scene);
        stageMainScreen.setResizable(false);
        stageMainScreen.show();

    }

    public static Stage getStageMainScreen() {
        return stageMainScreen;
    }

    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }


    public void initialize() {
//        gaugeRAM.setUnit("%");
//        gaugeRAM.setTitle("RAM");
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
        System.out.println("En MainScreenController" + messageFromTCPClient);

        if (messageFromTCPClient != null && !messageFromTCPClient.isEmpty()) {
            String[] splitMessage = messageFromTCPClient.split(",");
            double ramUsage = splitMessage.length > 0 && !splitMessage[0].isEmpty() ? Double.parseDouble(splitMessage[0]) : 0;
            double cpuUsage = splitMessage.length > 1 && !splitMessage[1].isEmpty() ? Double.parseDouble(splitMessage[1]) : 0;

            Platform.runLater(() -> {
                gaugeRAM.setValue(ramUsage);
                gaugeCPU.setValue(cpuUsage);
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

            vBoxServers.setStyle("-fx-padding: 20;");
            // Crea un nuevo botón y lo agrega al StackPane
            Button serverButton = new Button(result[0]);
            serverButton.setMaxWidth(Double.MAX_VALUE);
             serverButton.getStyleClass().add("servidores"); // Agrega la clase al botón

            serverButton.setOnAction(event -> {
                Platform.runLater(() -> serverButton.setStyle("-fx-background-color: #444444;"));
                executorService.submit(()-> updateGauges());
            });
            Platform.runLater(() -> {
                vBoxServers.setSpacing(10);
                vBoxServers.getChildren().add(serverButton);
            });
        }
    }

    private TextField addFieldToGrid(GridPane grid, String labelText, int row) {
        TextField textField = new TextField();
        grid.add(new Label(labelText), 0, row);
        grid.add(textField, 1, row);
        return textField;
    }
}