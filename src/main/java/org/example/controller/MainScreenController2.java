package org.example.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import eu.hansolo.medusa.Gauge;
import org.example.connection.Connection;
import org.example.connection.OpenConnections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainScreenController2 {

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

    private Button servidorSeleccionado = null;
    private Connection conexionSeleccionada = null;
    private Button botonActivo = null;


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

    @FXML
    private void initialize() {
        setupScrollPane();
        setupComboBox();
        setupGauges();
        setupChart();
        //startUpdateThread();
    }

    private void setupScrollPane() {
        scrollPaneServers.setContent(vBoxServers);
        scrollPaneServers.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        vBoxServers.setStyle("-fx-padding: 10 20;");
        vBoxServers.setSpacing(7);
    }

    private void setupComboBox() {
        comboBoxDisks.setValue("Seleccione un disco");
    }

    private void setupGauges() {
        setupGauge(gaugeRAM, Color.GREEN);
        setupGauge(gaugeCPU, Color.GREEN);
        gaugeDisk.setBarColor(Color.BLUE);
    }

    private void setupGauge(Gauge gauge, Color color) {
        gauge.setThreshold(75);
        gauge.setThresholdColor(Gauge.BRIGHT_COLOR);
        gauge.setThresholdVisible(true);
        gauge.setBarColor(color);
    }

    private void setupChart() {
        CategoryAxis xAxis = (CategoryAxis) chartRed.getXAxis();
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        chartRed.setCreateSymbols(false);
        series = new XYChart.Series<>();
        chartRed.getData().add(series);
    }

    public void addServer(ActionEvent actionEvent) {
        String[] result = showDialogAddServer(actionEvent);
        if (result != null) {
            addButton(result[0]);
        }
    }

    private TextField addFieldToGrid(GridPane grid, String labelText, int row) {
        TextField textField = new TextField();
        grid.add(new Label(labelText), 0, row);
        grid.add(textField, 1, row);
        return textField;
    }

    public String[] showDialogAddServer(ActionEvent actionEvent) {
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
        dialog.showAndWait();


        if (dialog.getResult() != null) {

            Connection nuevaConexion = new Connection(dialog.getResult()[0], dialog.getResult()[1], Integer.parseInt(dialog.getResult()[2]));
            executorService.submit(() -> {
                Socket socket = nuevaConexion.createConexion();
                if (socket != null) {
                    OpenConnections.addConnection(nuevaConexion);
                    Platform.runLater(this::refreshButtons);
                    //addServerToUI(dialog.getResult());
                }
            });
        }
        return dialog.getResult();
    }

    public void refreshButtons() {
        vBoxServers.getChildren().clear();
        for (Connection connection : OpenConnections.getConnections()) {
            Button serverButton = new Button(connection.getServerAlias());
            serverButton.setMaxWidth(Double.MAX_VALUE);
            serverButton.getStyleClass().add("servidores"); // Agrega la clase al botón

            serverButton.setOnAction(event -> {
                Platform.runLater(() -> serverButton.setStyle("-fx-background-color: #444444;"));
                servidorSeleccionado = serverButton;
                conexionSeleccionada = connection;
                executorService.submit(this::actualizarIndicadores);
            });

            // Si la conexión está recibiendo datos, establece el color del botón en verde
            if (connection.isConnected()) {
                serverButton.setStyle("-fx-background-color: #00FF00;");
            }

            vBoxServers.getChildren().add(serverButton);
        }
    }

    private void recieveData(Connection connection, Button button) {
        try {
            if (connection.isReceivingData()) {
                synchronized (connection.getLock()) {
                    connection.getLock().notify(); // Reanuda la recepción de datos
                }
                connection.setReceivingData(false); // Establece receivingData en false
                Platform.runLater(() -> {
                    button.setStyle("-fx-background-color: #ffffff;");
                    button.setTextFill(javafx.scene.paint.Color.BLACK);
                }); // Restaura el color original del botón
            } else {
                if (conexionSeleccionada != null && conexionSeleccionada != connection) {
                    synchronized (conexionSeleccionada.getLock()) {
                        conexionSeleccionada.getLock().notify(); // Reanuda la recepción de datos en la conexión anterior
                    }
                }
                Socket socket = connection.getSocket();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Platform.runLater(() -> {
                    button.setStyle("-fx-background-color: #00FF00;"); // Cambia el color del botón a verde

                });

                connection.setReceivingData(true); // Establece receivingData en true
                conexionSeleccionada = connection; // Actualiza la conexión seleccionada
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    private void addButton(String serverAlias) {
        Button serverButton = new Button(serverAlias);
        serverButton.setMaxWidth(Double.MAX_VALUE);
        serverButton.getStyleClass().add("servidores"); // Agrega la clase al botón

        Connection connection = OpenConnections.getConnections().stream()
                .filter(conn -> conn.getServerAlias().equals(serverAlias))
                .findFirst()
                .orElse(null);

        if (connection != null) {
            serverButton.setOnAction(event -> {
                Platform.runLater(() -> serverButton.setStyle("-fx-background-color: #444444;"));
                executorService.submit(() -> actualizarIndicadores());
            });
        }

        vBoxServers.getChildren().add(serverButton);
    }
//
//    private void addServerToUI(String[] result) {
//        Button serverButton = new Button(result[0]);
//        serverButton.setMaxWidth(Double.MAX_VALUE);
//        serverButton.getStyleClass().add("servidores"); // Agrega la clase al botón
//
//        serverButton.setOnAction(event -> {
//            Platform.runLater(() -> serverButton.setStyle("-fx-background-color: #444444;"));
//            executorService.submit(() -> actualizarIndicadores());
//        });
//        Platform.runLater(() -> {
//            vBoxServers.getChildren().add(serverButton);
//        });
//    }

//    private void startUpdateThread() {
//        new Thread(() -> {
//            while (true) {
//                actualizarIndicadores();
//                sleepOneSecond();
//            }
//        }).start();
//    }
//
//    private void sleepOneSecond() {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


    public void actualizarIndicadores() {
        String mensajeDelServidor = Connection.getMessage();
        if (esMensajeValido(mensajeDelServidor)) {
            procesarMensaje(mensajeDelServidor);
        }
    }

    private boolean esMensajeValido(String mensaje) {
        return mensaje != null && !mensaje.isEmpty();
    }

    private void procesarMensaje(String mensaje) {
        String[] mensajeDividido = mensaje.split(",");
        double usoRam = parsearDoubleODefault(mensajeDividido, 1, 0);
        double usoCpu = parsearDoubleODefault(mensajeDividido, 2, 0);
        double velocidadRed = parsearDoubleODefault(mensajeDividido, 4, 0);
        actualizarInformacionDiscos(mensajeDividido);
        actualizarUIIndicadores(usoRam, usoCpu, velocidadRed);
    }

    private double parsearDoubleODefault(String[] array, int indice, double valorPorDefecto) {
        return array.length > indice && !array[indice].isEmpty() ? Double.parseDouble(array[indice]) : valorPorDefecto;
    }

    private void actualizarInformacionDiscos(String[] mensajeDividido) {
        String[] usoDisco = parsearUsoDisco(mensajeDividido);
        Map<String, String[]> mapaInfoDisco = construirMapaInfoDisco(usoDisco);
        agregarDiscosAComboBox(mapaInfoDisco);
        configurarListenerSeleccionDisco(mapaInfoDisco);
    }

    private String[] parsearUsoDisco(String[] mensajeDividido) {
        return mensajeDividido.length > 3 && !mensajeDividido[3].isEmpty() ? mensajeDividido[3].replace("[", "").replace("]", "").split("#") : new String[0];
    }

    private Map<String, String[]> construirMapaInfoDisco(String[] usoDisco) {
        Map<String, String[]> mapaInfoDisco = new HashMap<>();
        for (String itemUsoDisco : usoDisco) {
            String[] disco = dividirDiscos(itemUsoDisco);
            mapaInfoDisco.put(disco[0], new String[]{disco[1], disco[2], disco[3]});
        }
        return mapaInfoDisco;
    }

    public static String[] dividirDiscos(String discos) {
        String[] discosDivididos = discos.split("_");
        return discosDivididos;
    }

    private void agregarDiscosAComboBox(Map<String, String[]> mapaInfoDisco) {
        Platform.runLater(() -> {
            for (String nombreDisco : mapaInfoDisco.keySet()) {
                if (!comboBoxDisks.getItems().contains(nombreDisco)) {
                    comboBoxDisks.getItems().add(nombreDisco);
                }
            }
        });
    }

    private void configurarListenerSeleccionDisco(Map<String, String[]> mapaInfoDisco) {
        comboBoxDisks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, nuevoValor) -> {
            String[] infoDisco = mapaInfoDisco.get(nuevoValor);
            actualizarCamposDiscoYMedidor(infoDisco);
        });
    }

    private void actualizarCamposDiscoYMedidor(String[] infoDisco) {
        if (infoDisco != null) {
            Platform.runLater(() -> {
                textFieldDisksFormat.setText(infoDisco[0]);
                double capacidadDisco = Double.parseDouble(infoDisco[1]);
                String capacidadDiscoEnGBFormateada = String.format("%.2f GB", capacidadDisco / 1024 / 1024 / 1024);
                textFieldDiskCapacity.setText(capacidadDiscoEnGBFormateada);
                gaugeDisk.setValue(Double.parseDouble(infoDisco[2]));
            });
        }
    }

    private void actualizarUIIndicadores(double usoRam, double usoCpu, double velocidadRed) {
        Platform.runLater(() -> {
            gaugeRAM.setValue(usoRam);
            gaugeCPU.setValue(usoCpu);
            series.getData().add(new XYChart.Data<>(String.valueOf(System.currentTimeMillis()), velocidadRed));
            if (series.getData().size() > 11) {
                series.getData().remove(0);
            }
            labelMbps.setText(String.valueOf(velocidadRed) + " Mbps");
        });
    }

}