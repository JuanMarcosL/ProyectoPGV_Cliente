package org.example.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.AppMain;
import org.example.connection.TCPClient;
import eu.hansolo.medusa.Gauge;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    public Label labelMbps;

    @FXML
    private Gauge gaugeRAM;

    @FXML
    private BorderPane borderPaneServers;

    @FXML
    private VBox vBoxServers;

    @FXML
    private ScrollPane scrollPaneServers;

    private XYChart.Series<String, Number> series;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private static Thread currentThread;

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

        scrollPaneServers.setContent(vBoxServers);
        scrollPaneServers.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        comboBoxDisks.setValue("Seleccione un disco");

        gaugeRAM.setThreshold(75);
        gaugeRAM.setThresholdColor(Gauge.BRIGHT_COLOR);
        gaugeRAM.setThresholdVisible(true);
        gaugeRAM.setBarColor(Color.GREEN);

        gaugeCPU.setThreshold(75);
        gaugeCPU.setThresholdColor(Gauge.BRIGHT_COLOR);
        gaugeCPU.setThresholdVisible(true);
        gaugeCPU.setBarColor(Color.GREEN);

        CategoryAxis xAxis = (CategoryAxis) chartRed.getXAxis();
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        chartRed.setCreateSymbols(false);
        series = new XYChart.Series<>();
        chartRed.getData().add(series);

        gaugeDisk.setBarColor(Color.BLUE);
    }

    public void updateGauges(String serverMessage) {
        String messageFromTCPClient = serverMessage;
        System.out.println("En MainScreenController" + messageFromTCPClient);

        if (messageFromTCPClient != null && !messageFromTCPClient.isEmpty()) {

            String[] splitMessage = messageFromTCPClient.split(",");

            double ramUsage = splitMessage.length > 0 && !splitMessage[0].isEmpty() ? Double.parseDouble(splitMessage[0]) : 0;
            double cpuUsage = splitMessage.length > 1 && !splitMessage[1].isEmpty() ? Double.parseDouble(splitMessage[1]) : 0;
            double redSpeed = splitMessage.length > 3 && !splitMessage[3].isEmpty() ? Double.parseDouble(splitMessage[3]) : 0;

            String[] diskUsage = splitMessage.length > 2 && !splitMessage[2].isEmpty() ? splitMessage[2].replace("[", "").replace("]", "").split("#") : new String[0];

            Map<String, String[]> diskInfoMap = new HashMap<>();

            boolean disksChanged = false;

            for (int i = 0; i < diskUsage.length; i++) {
                String[] disk = dividirDiscos(diskUsage[i]);
                diskInfoMap.put(disk[0], new String[]{disk[1], disk[2], disk[3]});
                if (!comboBoxDisks.getItems().contains(disk[0])) {
                    disksChanged = true;
                    break;
                }
            }

            if (disksChanged || comboBoxDisks.getItems().isEmpty()) {
                Platform.runLater(() -> {
                    comboBoxDisks.getItems().clear();
                    for (int i = 0; i < diskUsage.length; i++) {
                        String[] disk = dividirDiscos(diskUsage[i]);
                        comboBoxDisks.getItems().add(disk[0]);
                    }
                });
            }

            comboBoxDisks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                // Obtener la información del disco seleccionado del HashMap
                String[] diskInfo = diskInfoMap.get(newValue);

                // Actualizar los campos de texto y el medidor con la información del disco seleccionado
                if (diskInfo != null) {
                    textFieldDisksFormat.setText(diskInfo[0]);
                    double diskCapacity = Double.parseDouble(diskInfo[1]); // Asume que diskInfo[1] está en MB
                    double diskCapacityInGB = diskCapacity / 1024 / 1024 / 1024;
                    String diskCapacityInGBFormatted = String.format("%.2f GB", diskCapacityInGB);
                    textFieldDiskCapacity.setText(diskCapacityInGBFormatted);
//                textFieldDiskCapacity.setText(diskInfo[1]);
                    gaugeDisk.setValue(Double.parseDouble(diskInfo[2]));
                }
            });


            Platform.runLater(() -> {
                gaugeRAM.setValue(ramUsage);
                gaugeCPU.setValue(cpuUsage);


                series.getData().add(new XYChart.Data<>(String.valueOf(System.currentTimeMillis()), redSpeed));
                if (series.getData().size() > 11) {
                    series.getData().remove(0);
                }
                labelMbps.setText(String.valueOf(redSpeed) + " Mbps");

            });

        }
    }

    public static String[] dividirDiscos(String discos) {
        String[] discosDivididos = discos.split("_");
        return discosDivididos;
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

        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        if (addButton instanceof Button) {
            ((Button) addButton).getStyleClass().add("botonazo");
        }

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButtonType) {
                return new String[]{alias.getText(), ip.getText(), puerto.getText()};
            }
            return null;
        });

        String[] result = dialog.showAndWait().orElse(null);
        if (result != null) {
            String host = result[1].equalsIgnoreCase("localhost") ? "127.0.0.1" : result[1];
            new Thread(() -> {
                TCPClient tcpClient = new TCPClient(result[0], host, Integer.parseInt(result[2]));
            }).start();

            vBoxServers.setStyle("-fx-padding: 10 30 10 20; -fx-background-color: #555;");


            Button serverButton = new Button(result[0]);
            serverButton.setMaxWidth(Double.MAX_VALUE);
            serverButton.getStyleClass().add("servidores"); // Agrega la clase al botón

            // Asignar la dirección IP como atributo personalizado del botón
            serverButton.setUserData(result[1]);

            vBoxServers.setSpacing(7);

            vBoxServers.getChildren().add(serverButton);

            serverButton.setOnAction(event -> {
                // Detener el hilo de ejecución anterior (si existe)
                if (currentThread != null && currentThread.isAlive()) {
                    currentThread.interrupt();
                }
                // Iniciar un nuevo hilo de ejecución para recibir mensajes del servidor
                currentThread = new Thread(() -> {

                    // Recuperar la dirección IP del botón
                    String ipAddress = (String) serverButton.getUserData();
                    while (!Thread.currentThread().isInterrupted()) {
                        // Obtener los mensajes correspondientes a la dirección IP
                        Map<String, String> serverMessages = TCPClient.getServerMessages();
                        String serverMessage = serverMessages.get(ipAddress);
                        if (serverMessage != null) {
                            // Actualizar la interfaz con los valores del servidor
                            Platform.runLater(() -> {
                                updateGauges(serverMessage);
                            });
                        }
                        try {
                            Thread.sleep(1000); // Esperar 1 segundo antes de la próxima actualización
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
                        }
                    }
                });
                currentThread.start();

                Platform.runLater(() -> {
                    gaugeDisk.setValue(0);
                    textFieldDisksFormat.clear();
                    textFieldDiskCapacity.clear();
                    comboBoxDisks.setValue("Seleccione un disco");

                    for (Node node : vBoxServers.getChildren()) {
                        if (node instanceof Button) {
                            node.getStyleClass().remove("button-selected");
                        }
                    }

                    // Agregar la clase CSS 'button-selected' al botón actual
                    serverButton.getStyleClass().add("button-selected");
                });
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