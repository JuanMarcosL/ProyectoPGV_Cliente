package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class UmbralController {
    @FXML
    public Slider sliderCPU;
    @FXML
    public Slider sliderRam;
    @FXML
    public Button botonAceptarUmbral;
    @FXML
    public Label labelRAM;
    @FXML
    public Label labelCPU;
    @FXML
    public TextField textFieldRed;

    MainScreenController mainScreenController;

    public void initialize() {}
    public void initializeControls() {

        sliderRam.setValue(mainScreenController.getUmbralRam());
        labelRAM.setText(String.valueOf(mainScreenController.getUmbralRam()) + " %");
        sliderRam.valueProperty().addListener((observable, oldValue, newValue) -> {
            labelRAM.setText(String.valueOf(newValue.intValue()) + " %");
        });

        sliderCPU.setValue(mainScreenController.getUmbralCpu());
        labelCPU.setText(String.valueOf(mainScreenController.getUmbralCpu()) + " %");
        sliderCPU.valueProperty().addListener((observable, oldValue, newValue) -> {
            labelCPU.setText(String.valueOf(newValue.intValue()) + " %");
        });


        textFieldRed.setText(String.valueOf(mainScreenController.getUmbralRed()));


    }

    public void setMainScreenController(MainScreenController mainScreenController) {
        this.mainScreenController = mainScreenController;
    }

    public void aceptarUmbrales(ActionEvent actionEvent) {

        try {
            if (textFieldRed.getText().isEmpty() || Integer.parseInt(textFieldRed.getText()) < 0) {
                mostrarMensajeError("Error", "El umbral de red debe ser un número entero igual o mayor a 0");
                return;
            }
            mainScreenController.setUmbralRam((int) sliderRam.getValue());
            mainScreenController.setUmbralCpu((int) sliderCPU.getValue());
            mainScreenController.setUmbralRed(Integer.parseInt(textFieldRed.getText()));

            // Cerrar la ventana
            ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            mostrarMensajeError("Error", "El umbral de red debe ser un número entero igual o mayor a 0");
            return;
        }
    }

    private void mostrarMensajeError(String error, String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(error);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    public void cancelar(ActionEvent actionEvent) {
        if (mostrarMensajeConfirmacion("Cancelar", "¿Estás seguro de que quieres cancelar?")) {

            try {
                ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
                MainScreenController.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean mostrarMensajeConfirmacion(String cancelar, String s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(cancelar);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();

        return alert.getResult().getText().equals("Aceptar");
    }
}
