package org.example.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.AppMain;
import org.example.connection.TCPClient;
import eu.hansolo.medusa.Gauge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    public Button botonMinimizar;
    @FXML
    public ImageView imagenMenu;
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
    private ContextMenu contextMenu;
    private boolean menuOpen = false;

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

        contextMenu = new ContextMenu();
        MenuItem cerrarSesion = new MenuItem("Cerrar sesión");
        cerrarSesion.setOnAction(event -> {
            boolean confirmed = showConfirmationDialog("Se cerrará la sesión actual", "¿Estás seguro?");
            if (confirmed) {
                // Cerrar la ventana actual
                Stage currentStage = (Stage) imagenMenu.getScene().getWindow();
                currentStage.close();

                // Abrir la ventana de inicio de sesión
                try {
                    showLoginScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        contextMenu.getItems().addAll(cerrarSesion);
        contextMenu.setOnHidden(event -> menuOpen = false);
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

    public void addServer(ActionEvent actionEvent) throws Exception {
        showDialogAddServer(actionEvent);
    }

    public void showDialogAddServer(ActionEvent actionEvent) throws Exception {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Añadir servidor");
        dialog.getDialogPane().getStyleClass().add("dialog-background");


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


        String host = null;
        String[] result = null;
        boolean validData = false;

        do {
            result = dialog.showAndWait().orElse(null);
            if (result != null) {

                try {
                    host = result[1].equalsIgnoreCase("localhost") ? "127.0.0.1" : result[1];
                    validateServerData(result[0], host, result[2]);
                    validData = true;
                    if (testConnection(host, Integer.parseInt(result[2]))) {
                        // Si la conexión es exitosa, agregar el botón del servidor

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Conexión exitosa");
                        alert.setHeaderText(null);
                        alert.setContentText("Se ha establecido una conexión con el servidor.");
                        alert.showAndWait();
                        String[] finalResult = result;
                        String finalHost = host;
                        new Thread(() -> {
                            TCPClient tcpClient = new TCPClient(finalResult[0], finalHost, Integer.parseInt(finalResult[2]));
                        }).start();

                        vBoxServers.setStyle("-fx-padding: 10 30 10 20; -fx-background-color: #555;");


                        Button serverButton = new Button(result[0]);
                        serverButton.setMaxWidth(Double.MAX_VALUE);
                        serverButton.getStyleClass().add("servidores"); // Agrega la clase al botón

                        // Asignar la dirección IP como atributo personalizado del botón
                        serverButton.setUserData(host);

                        vBoxServers.setSpacing(7);

                        vBoxServers.getChildren().add(serverButton);


                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem deleteItem = new MenuItem("Eliminar servidor");
                        contextMenu.getItems().add(deleteItem);

                        // Establecer el menú contextual en el botón del servidor
                        serverButton.setContextMenu(contextMenu);

                        // Agregar un manejador de eventos al elemento de menú
                        String finalHost1 = host;


                        deleteItem.setOnAction(event -> {

                            if (serverButton.getStyleClass().contains("button-selected")) {
                                // Mostrar un cuadro de diálogo de error
                                Alert alerta = new Alert(Alert.AlertType.ERROR);
                                alerta.setTitle("Error");
                                alerta.setHeaderText("No se puede eliminar el servidor");
                                alerta.setContentText("No puedes eliminar este servidor porque se está visualizando actualmente");
                                alerta.showAndWait();
                            } else {

                                boolean confirmed = showConfirmationDialog("Estás a punto de eliminar el servidor", "¿Estás seguro de que quieres continuar?");

                                if (confirmed) {
                                    vBoxServers.getChildren().remove(serverButton);
                                    TCPClient.removeServerMessage(finalHost1);
                                }
                            }
                        });

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
                    } else {
                        throw new Exception("No se pudo establecer una conexión con el servidor.");
                    }
                } catch (Exception e) {
                    // Mostrar un cuadro de diálogo de error con el mensaje de la excepción
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error al agregar el servidor");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            } else {
                break; // Si el usuario canceló el diálogo, romper el bucle
            }
        } while (!validData);
    }


    private TextField addFieldToGrid(GridPane grid, String labelText, int row) {
        TextField textField = new TextField();
        grid.add(new Label(labelText), 0, row);
        grid.add(textField, 1, row);
        return textField;
    }

    private boolean showConfirmationDialog(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void validateServerData(String alias, String ip, String port) throws Exception {
        // Comprobar que ninguno de los campos esté vacío

        if (alias == null || alias.isEmpty()) {
            throw new Exception("El campo Alias no puede estar vacío.");
        }
        if (ip == null || ip.isEmpty()) {
            throw new Exception("El campo IP no puede estar vacío.");
        }
        if (port == null || port.isEmpty()) {
            throw new Exception("El campo Puerto no puede estar vacío.");
        }

        // Comprobar que el puerto sea un número entero válido
        try {
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new Exception("El puerto debe ser un número entero válido.");
        }

        // Comprobar que la IP y el alias no se repitan con alguna agregada previamente
        for (Node node : vBoxServers.getChildren()) {
            if (node instanceof Button) {
                Button serverButton = (Button) node;
                if (serverButton.getText().equals(alias)) {
                    throw new Exception("El alias ya está en uso.");
                }
                if (serverButton.getUserData().equals(ip)) {
                    throw new Exception("Ya hay un servidor con esa IP");
                }
            }
        }
    }

    private boolean testConnection(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void abrirMenu(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) { // Verifica si se hizo clic con el botón izquierdo
            if (!menuOpen) {
                contextMenu.show(imagenMenu, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                menuOpen = true;
            } else {
                contextMenu.hide();
                menuOpen = false;
            }
        }
    }

    private void showLoginScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("/org/example/LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        //scene.getStylesheets().add(AppMain.class.getResource("..\\..\\CSS\\styles.css").toExternalForm()); // Carga la hoja de estilo
        Stage loginStage = new Stage();
        loginStage.setTitle("ServiStat");
        loginStage.setScene(scene);
        loginStage.setResizable(false);
        loginStage.show();
    }

    public void minimizarAPP(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();

        // Crear una nueva transición de tiempo
        Timeline timeline = new Timeline();

        // Agregar una secuencia de acciones a la transición
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(0.2), // Duración de la transición
                        event -> {
                            stage.setIconified(true);
                            stage.opacityProperty().set(1); // Restaurar la opacidad cuando se minimiza
                        },
                        new KeyValue(stage.opacityProperty(), 0.0) // Cambiar la propiedad de opacidad a 0
                )
        );

        // Iniciar la transición
        timeline.play();

        // Añadir un listener al estado de la ventana
        stage.iconifiedProperty().addListener((obs, wasMinimized, isNowMinimized) -> {
            if (!isNowMinimized) {
                stage.opacityProperty().set(1); // Restaurar la opacidad cuando se desminimiza
            }
        });
    }

    public void closeApp(ActionEvent actionEvent) {
        boolean confirmed = showConfirmationDialog("Se cerrará la aplicación", "¿Quieres continuar?");
        if (confirmed) {
            Platform.exit();
            System.exit(0);
        }
    }
}

