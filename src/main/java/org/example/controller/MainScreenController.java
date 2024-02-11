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
import javafx.scene.Parent;
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
import javafx.stage.StageStyle;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.example.AppMain;
import org.example.connection.TCPClient;
import eu.hansolo.medusa.Gauge;
import org.example.models.User;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Esta clase controla la funcionalidad de la pantalla principal.
 */
public class MainScreenController {

    public MainScreenController getMainScreenController() {
        return this;
    }

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

    private static Thread currentThread;
    private ContextMenu contextMenu = new ContextMenu();
    private boolean menuOpen = false;

    private String aliasServidor = null;

    //Valor inicial de los umbrales
    private int umbralRam = 75;
    private int umbralCpu = 10;
    private int umbralRed = 10;
    /**
     * Este método muestra la pantalla principal.
     * @throws IOException Si ocurre un error al cargar la interfaz de usuario.
     */
    public static void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(AppMain.class.getResource("..\\..\\CSS\\styles.css").toExternalForm()); // Carga la hoja de estilo
        stageMainScreen.setTitle("ServiStat");
        stageMainScreen.setScene(scene);
        stageMainScreen.setResizable(false);
        stageMainScreen.show();
    }

    /**
     * Este método devuelve el escenario de la pantalla principal.
     * @return El escenario de la pantalla principal.
     */
    public static Stage getStageMainScreen() {
        return stageMainScreen;
    }

    /**
     * Este método inicializa los componentes de la interfaz de usuario.
     */
    public void initialize() {

        initializeGauges();
        initializeGraphic();
        initializeScrollPane();
        initializeComboBox();
        initializeContextMenu();
    }

    /**
     * Este método genera un reporte.
     * @param generarReporte El elemento de menú para generar el reporte.
     */
    public void generarReporte (MenuItem generarReporte) {

        generarReporte.setOnAction(this::generarReporte);
    }

    /**
     * Este método cierra la sesión actual.
     * @param cerrarSesion El elemento de menú para cerrar la sesión.
     */
        public void cerrarSesion(MenuItem cerrarSesion){
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
    }
    /**
     * Este método inicializa el gráfico.
     */
    public void initializeGraphic(){
        CategoryAxis xAxis = (CategoryAxis) chartRed.getXAxis();
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        chartRed.setCreateSymbols(false);
        series = new XYChart.Series<>();
        chartRed.getData().add(series);
    }
    /**
     * Este método inicializa los medidores.
     */
    public void initializeGauges() {
        gaugeRAM.setThreshold(umbralRam);
        gaugeRAM.setThresholdColor(Gauge.BRIGHT_COLOR);
        gaugeRAM.setThresholdVisible(true);
        gaugeRAM.setBarColor(Color.GREEN);

        gaugeCPU.setThreshold(umbralCpu);
        gaugeCPU.setThresholdColor(Gauge.BRIGHT_COLOR);
        gaugeCPU.setThresholdVisible(true);
        gaugeCPU.setBarColor(Color.GREEN);

        gaugeDisk.setBarColor(Color.BLUE);
    }

    /**
     * Este método inicializa el panel de desplazamiento.
     */
    private void initializeScrollPane() {
        scrollPaneServers.setContent(vBoxServers);
        scrollPaneServers.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    /**
     * Este método inicializa el cuadro combinado.
     */
    private void initializeComboBox() {

        comboBoxDisks.setValue("Seleccione un disco");
    }

    /**
     * Este método inicializa el menú contextual.
     */
    private void initializeContextMenu() {
        MenuItem configurarUmbrales = new MenuItem("Configurar umbrales");
        MenuItem generarReporte = new MenuItem("Generar reporte");
        MenuItem cerrarSesion = new MenuItem("Cerrar sesión");
        MenuItem ayuda = new MenuItem("Ayuda");
        MenuItem eliminarUsuario = new MenuItem("Eliminar usuario");

        contextMenu.getItems().addAll(configurarUmbrales, generarReporte, ayuda, eliminarUsuario, cerrarSesion);
        contextMenu.setOnHidden(event -> menuOpen = false);

        configurarUmbrales(configurarUmbrales);
        generarReporte(generarReporte);
        cerrarSesion(cerrarSesion);
        mostrarAyuda(ayuda);
        eliminarUsuario(eliminarUsuario);
    }

    public void configurarUmbrales(MenuItem configurarUmbrales) {
        configurarUmbrales.setOnAction(event -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/SetUmbrals.fxml"));
                Parent root = fxmlLoader.load();
                UmbralController umbralController = fxmlLoader.getController();
                umbralController.setMainScreenController(this); // Pasar la instancia de MainScreenController a UmbralController
                umbralController.initializeControls(); // Nueva línea: inicializar los controles después de establecer mainScreenController

                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.centerOnScreen();

                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }



    public void eliminarUsuario(MenuItem eliminarUsuario) {
        eliminarUsuario.setOnAction(event -> {
            boolean confirmed = showConfirmationDialog("Eliminar usuario", "¿Estás seguro de que quieres eliminar tu cuenta?");
            if (confirmed) {
                User.getUsers().remove(LogInController.getUsuarioActual());
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
    }

    public void mostrarAyuda(MenuItem ayuda) {
        ayuda.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ayuda");
            alert.setHeaderText(null);
            alert.setContentText("Para obtener ayuda, comuníquese con el administrador del sistema.");
            alert.showAndWait();
        });
    }


    /**
     * Este método actualiza los medidores con los datos del servidor.
     * @param serverMessage El mensaje del servidor.
     */
    public void updateGauges(String serverMessage) {
        String messageFromTCPClient = serverMessage;
        System.out.println("En MainScreenController" + messageFromTCPClient);

        if (messageFromTCPClient != null && !messageFromTCPClient.isEmpty()) {
            String[] splitMessage = messageFromTCPClient.split(",");
            processUsageData(splitMessage);
            processDiskData(splitMessage);
            updateGaugesUI(splitMessage);
        }
    }

    /**
     * Este método procesa los datos de uso del servidor.
     * @param splitMessage El mensaje del servidor dividido en partes.
     */
    private void processUsageData(String[] splitMessage) {
        double ramUsage = splitMessage.length > 0 && !splitMessage[0].isEmpty() ? Double.parseDouble(splitMessage[0]) : 0;
        double cpuUsage = splitMessage.length > 1 && !splitMessage[1].isEmpty() ? Double.parseDouble(splitMessage[1]) : 0;
        double redSpeed = splitMessage.length > 3 && !splitMessage[3].isEmpty() ? Double.parseDouble(splitMessage[3]) : 0;

        if (ramUsage > umbralRam) {
            generarArchivoCSV(aliasServidor, "RAM", ramUsage);
        }
        if (cpuUsage > umbralCpu) {
            generarArchivoCSV(aliasServidor, "CPU", cpuUsage);
        }
        if (redSpeed < umbralRed) {
            generarArchivoCSV(aliasServidor, "Red", redSpeed);
        }
    }

    /**
     * Este método procesa los datos del disco del servidor.
     * @param splitMessage El mensaje del servidor dividido en partes.
     */
    private void processDiskData(String[] splitMessage) {
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
            updateComboBoxDisks(diskUsage);
        }

        comboBoxDisks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateDiskInfo(diskInfoMap.get(newValue));
        });
    }

    /**
     * Este método actualiza el cuadro combinado de discos.
     * @param diskUsage Los datos de uso del disco.
     */
    private void updateComboBoxDisks(String[] diskUsage) {
        Platform.runLater(() -> {
            comboBoxDisks.getItems().clear();
            for (int i = 0; i < diskUsage.length; i++) {
                String[] disk = dividirDiscos(diskUsage[i]);
                comboBoxDisks.getItems().add(disk[0]);
            }
        });
    }

    /**
     * Este método actualiza la información del disco.
     * @param diskInfo La información del disco.
     */
    private void updateDiskInfo(String[] diskInfo) {
        if (diskInfo != null) {
            textFieldDisksFormat.setText(diskInfo[0]);
            double diskCapacity = Double.parseDouble(diskInfo[1]); // Asume que diskInfo[1] está en MB
            double diskCapacityInGB = diskCapacity / 1024 / 1024 / 1024;
            String diskCapacityInGBFormatted = String.format("%.2f GB", diskCapacityInGB);
            textFieldDiskCapacity.setText(diskCapacityInGBFormatted);
            gaugeDisk.setValue(Double.parseDouble(diskInfo[2]));
        }
    }

    /**
     * Este método actualiza la interfaz de usuario de los medidores.
     * @param splitMessage El mensaje del servidor dividido en partes.
     */
    private void updateGaugesUI(String[] splitMessage) {
        double ramUsage = splitMessage.length > 0 && !splitMessage[0].isEmpty() ? Double.parseDouble(splitMessage[0]) : 0;
        double cpuUsage = splitMessage.length > 1 && !splitMessage[1].isEmpty() ? Double.parseDouble(splitMessage[1]) : 0;
        double redSpeed = splitMessage.length > 3 && !splitMessage[3].isEmpty() ? Double.parseDouble(splitMessage[3]) : 0;

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

    /**
     * Este método divide los datos del disco.
     * @param discos Los datos del disco.
     * @return Los datos del disco divididos.
     */
    public static String[] dividirDiscos(String discos) {
        String[] discosDivididos = discos.split("_");
        return discosDivididos;
    }

    /**
     * Este método agrega un servidor.
     * @param actionEvent El evento de acción.
     * @throws Exception Si ocurre un error al agregar el servidor.
     */
    public void addServer(ActionEvent actionEvent) throws Exception {
        showDialogAddServer(actionEvent);
    }

    /**
     * Este método muestra el diálogo para agregar un servidor.
     * @param actionEvent El evento de acción.
     */
    public void showDialogAddServer(ActionEvent actionEvent) {
        Dialog<String[]> dialog = createServerDialog();
        String[] result = null;
        boolean validData = false;

        do {
            result = dialog.showAndWait().orElse(null);
            if (result != null) {
                validData = processServerData(result);
            } else {
                break; // If the user cancels the dialog, break the loop
            }
        } while (!validData);
    }

    /**
     * Este método crea un diálogo para agregar un servidor.
     * @return El diálogo para agregar un servidor.
     */
    private Dialog<String[]> createServerDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Añadir servidor");
        dialog.getDialogPane().getStyleClass().add("dialog-background");

        GridPane grid = createGridPane(); // Aquí deberías crear el GridPane con los campos de texto
        TextField alias = (TextField) grid.getChildren().get(1); // Suponiendo que el primer campo de texto es para el alias
        TextField ip = (TextField) grid.getChildren().get(3); // Suponiendo que el segundo campo de texto es para la IP
        TextField puerto = (TextField) grid.getChildren().get(5); // Suponiendo que el tercer campo de texto es para el puerto

        dialog.getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Añadir", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        setupButtonStyle(dialog, addButtonType);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButtonType) {
                return new String[]{alias.getText(), ip.getText(), puerto.getText()};
            }
            return null;
        });

        return dialog;
    }
    /**
     * Este método crea un panel de cuadrícula para el diálogo de agregar servidor.
     * @return El panel de cuadrícula.
     */
    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField alias = addFieldToGrid(grid, "Alias:", 0);
        TextField ip = addFieldToGrid(grid, "Dirección IP:", 1);
        TextField puerto = addFieldToGrid(grid, "Puerto:", 2);

        return grid;
    }

    /**
     * Este método agrega un campo de texto al panel de cuadrícula.
     * @param grid El panel de cuadrícula.
     * @param label La etiqueta del campo de texto.
     * @param row La fila en la que se debe agregar el campo de texto.
     * @return El campo de texto.
     */
    private TextField addFieldToGrid(GridPane grid, String label, int row) {
        Label lbl = new Label(label);
        TextField textField = new TextField();
        grid.add(lbl, 0, row);
        grid.add(textField, 1, row);
        return textField;
    }

    /**
     * Este método configura el estilo del botón en el diálogo.
     * @param dialog El diálogo.
     * @param addButtonType El tipo de botón a configurar.
     */
    private void setupButtonStyle(Dialog<String[]> dialog, ButtonType addButtonType) {
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        if (addButton instanceof Button) {
            ((Button) addButton).getStyleClass().add("botonazo");
        }
    }

    /**
     * Este método procesa los datos del servidor.
     * @param result Los datos del servidor.
     * @return Verdadero si los datos son válidos, falso en caso contrario.
     */
    private boolean processServerData(String[] result) {
        try {
            String host = (result[1].equalsIgnoreCase("localhost")) ? "127.0.0.1" : result[1];
            validateServerData(result[0], host, result[2]);
            if (testConnection(host, Integer.parseInt(result[2]))) {
                handleSuccessfulConnection(result, host);
                return true;
            } else {
                throw new Exception("No se pudo establecer una conexión con el servidor.");
            }
        } catch (Exception e) {
            showErrorDialog("Error al agregar el servidor", e.getMessage());
            return false;
        }
    }

    /**
     * Este método maneja una conexión exitosa con el servidor.
     * @param result Los datos del servidor.
     * @param host El host del servidor.
     */
    private void handleSuccessfulConnection(String[] result, String host) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Conexión exitosa");
        alert.setHeaderText(null);
        alert.setContentText("Se ha establecido una conexión con el servidor.");
        alert.showAndWait();

        String finalHost = host;
        new Thread(() -> {
            TCPClient tcpClient = new TCPClient(result[0], finalHost, Integer.parseInt(result[2]));
        }).start();

        setupServerButton(result[0], host);
    }

    /**
     * Este método configura el botón del servidor.
     * @param alias El alias del servidor.
     * @param host El host del servidor.
     */
    private void setupServerButton(String alias, String host) {
        Button serverButton = new Button(alias);
        serverButton.setMaxWidth(Double.MAX_VALUE);
        serverButton.getStyleClass().add("servidores");
        serverButton.setUserData(host);

        vBoxServers.setSpacing(7);
        vBoxServers.getChildren().add(serverButton);

        ContextMenu contextMenu = createContextMenu(serverButton, host);
        serverButton.setContextMenu(contextMenu);

        serverButton.setOnAction(event -> handleServerButtonAction(serverButton));
    }

    /**
     * Este método crea un menú contextual para el botón del servidor.
     * @param serverButton El botón del servidor.
     * @param host El host del servidor.
     * @return El menú contextual.
     */
    private ContextMenu createContextMenu(Button serverButton, String host) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Eliminar servidor");
        contextMenu.getItems().add(deleteItem);

        deleteItem.setOnAction(event -> handleDeleteServerAction(serverButton, host));
        return contextMenu;
    }

    /**
     * Este método maneja la acción de eliminar servidor.
     * @param serverButton El botón del servidor.
     * @param host El host del servidor.
     */
    private void handleDeleteServerAction(Button serverButton, String host) {
        if (serverButton.getStyleClass().contains("button-selected")) {
            showErrorDialog("Error", "No puedes eliminar este servidor porque se está visualizando actualmente");
        } else {
            boolean confirmed = showConfirmationDialog("Estás a punto de eliminar el servidor", "¿Estás seguro de que quieres continuar?");
            if (confirmed) {
                vBoxServers.getChildren().remove(serverButton);
                TCPClient.removeServerMessage(host);
            }
        }
    }

    /**
     * Este método maneja la acción del botón del servidor.
     * @param serverButton El botón del servidor.
     */
    private void handleServerButtonAction(Button serverButton) {
        if (currentThread != null && currentThread.isAlive()) {
            currentThread.interrupt();
        }

        String ipAddress = (String) serverButton.getUserData();
        currentThread = new Thread(() -> listenForServerMessages(serverButton, ipAddress));
        currentThread.start();

        updateUI(serverButton);
    }

    /**
     * Este método escucha los mensajes del servidor.
     * @param serverButton El botón del servidor.
     * @param ipAddress La dirección IP del servidor.
     */
    private void listenForServerMessages(Button serverButton, String ipAddress) {
        while (!Thread.currentThread().isInterrupted()) {
            Map<String, String> serverMessages = TCPClient.getServerMessages();
            String serverMessage = serverMessages.get(ipAddress);
            if (serverMessage != null) {
                aliasServidor = serverButton.getText();
                Platform.runLater(() -> updateGauges(serverMessage));
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Este método actualiza la interfaz de usuario.
     * @param serverButton El botón del servidor.
     */
    private void updateUI(Button serverButton) {
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

            serverButton.getStyleClass().add("button-selected");
        });
    }

    /**
     * Este método muestra un diálogo de error.
     * @param title El título del diálogo.
     * @param message El mensaje del diálogo.
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Este método muestra un diálogo de confirmación.
     * @param title El título del diálogo.
     * @param message El mensaje del diálogo.
     * @return Verdadero si el usuario confirma, falso en caso contrario.
     */
    private boolean showConfirmationDialog(String title, String message) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle(title);
        confirmation.setHeaderText(null);
        confirmation.setContentText(message);
        Optional<ButtonType> result = confirmation.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Este método valida los datos del servidor.
     * @param alias El alias del servidor.
     * @param ip La dirección IP del servidor.
     * @param port El puerto del servidor.
     * @throws Exception Si los datos no son válidos.
     */
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

    /**
     * Este método prueba la conexión con el servidor.
     * @param host El host del servidor.
     * @param port El puerto del servidor.
     * @return Verdadero si la conexión es exitosa, falso en caso contrario.
     */
    private boolean testConnection(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Este método abre el menú.
     * @param mouseEvent El evento del ratón.
     */
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

    /**
     * Este método muestra la pantalla de inicio de sesión.
     * @throws IOException Si ocurre un error al cargar la interfaz de usuario.
     */
    private void showLoginScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("/org/example/LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage loginStage = new Stage();
        loginStage.setTitle("ServiStat");
        loginStage.setScene(scene);
        loginStage.setResizable(false);
        loginStage.show();
    }

    /**
     * Este método minimiza la aplicación.
     * @param actionEvent El evento de acción.
     */
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

    /**
     * Este método cierra la aplicación.
     * @param actionEvent El evento de acción.
     */
    public void closeApp(ActionEvent actionEvent) {
        boolean confirmed = showConfirmationDialog("Se cerrará la aplicación", "¿Quieres continuar?");
        if (confirmed) {
            Platform.exit();
            System.exit(0);
        }
    }

    /**
     * Este método genera un archivo CSV.
     * @param aliasServidor El alias del servidor.
     * @param tipoDato El tipo de dato.
     * @param valor El valor del dato.
     */
    public void generarArchivoCSV(String aliasServidor, String tipoDato, double valor) {
        String nombreArchivo = "src/main/resources/CSV/reporte.csv";
       // File archivo = new File(nombreArchivo);
        FileWriter archivoCSV = null;

        try {
            archivoCSV = new FileWriter(nombreArchivo, true);

            // Escribir los datos en el archivo
            archivoCSV.append(aliasServidor);
            archivoCSV.append(",");
            archivoCSV.append(tipoDato);
            archivoCSV.append(",");
            archivoCSV.append(String.valueOf(valor));
            archivoCSV.append("\n");

            System.out.println("CSV generado exitosamente");

        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo CSV");
            e.printStackTrace();
        } finally {
            try {
                archivoCSV.flush();
                archivoCSV.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar el archivo CSV");
                e.printStackTrace();
            }
        }
    }

    /**
     * Este método genera un reporte.
     * @param actionEvent El evento de acción.
     */
    public void generarReporte(ActionEvent actionEvent) {
        try {
            // Cargar el archivo .jasper
            String reportSrcFile = "src/main/resources/Jasper/reporte.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);

            // Crear el data source
            String csvPath = "src/main/resources/CSV/reporte.csv";
            JRCsvDataSource dataSource = new JRCsvDataSource(csvPath);
            dataSource.setUseFirstRowAsHeader(true);
            dataSource.setFieldDelimiter(',');
            dataSource.setRecordDelimiter("\n");

            // Crear los parámetros del reporte
            Map<String, Object> parameters = new HashMap<>();

            // Llenar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            File outDir = new File("src/main/resources/jasperoutput");
            outDir.mkdirs();

            JasperExportManager.exportReportToPdfFile(jasperPrint, "src/main/resources/jasperoutput/ReporteServidores.pdf");

            System.out.println("Informe generado con éxito en la ruta: " + outDir.getPath() + " /EmailsReport.pdf");
            // Mostrar el reporte en un JasperViewer
            try {
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
                jasperViewer.setTitle("Informe de Servidores");
                jasperViewer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                jasperViewer.toFront();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void setUmbralRam(int umbralRam) {
        this.umbralRam = umbralRam;
    }
    public void setUmbralCpu(int umbralCpu) {
        this.umbralCpu = umbralCpu;
    }
    public void setUmbralRed(int umbralRed) {
        this.umbralRed = umbralRed;
    }
    public int getUmbralRam() {
        return umbralRam;
    }
    public int getUmbralCpu() {
        return umbralCpu;
    }
    public int getUmbralRed() {
        return umbralRed;
    }
}

