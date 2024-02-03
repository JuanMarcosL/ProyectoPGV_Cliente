//package org.example.Utils;
//
//
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//public class recogerDatos {
//    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//    private String usoRAMStr;
//    private String velocidadInternetStr;
//
//    //private String temperaturaCPUStr;
//    private String leerUSBStr;
//    private boolean velocidadInternetRecogida = false;  // Controla si la información ya se recogió
//
//    public StringBuilder getMensaje() {
//        return mensaje;
//    }
//
//    private StringBuilder mensaje = new StringBuilder();
//
//
//
//    public void iniciarMonitorizacion() {
//        scheduler.scheduleAtFixedRate(this::recogerYEnviarDatos, 0, 1, TimeUnit.SECONDS);
//    }
//
//    private void recogerYEnviarDatos() {
//        // Carga de CPU
//        cargaCPU obtenerCarga = new cargaCPU();
//        String cargaCPUStr = obtenerCarga.obtenerCargaCPU();
//
//        // Uso de RAM
//        usoRAM usoDeRAM = new usoRAM();
//        usoDeRAM.iniciarMonitorizacion();
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        usoRAMStr = usoDeRAM.obtenerUsoRAM();
//
//        // Velocidad de internet
//        if (!velocidadInternetRecogida) {
//            velocidadInternet velocInternet = new velocidadInternet();
//            try {
//                velocInternet.obtenerVelocidadInternet();
//                velocInternet.recogerYEnviarVelocidadInternet();
//                velocidadInternetStr = velocInternet.obtenerVelocidadInternet();
//                velocidadInternetRecogida = true;  // Marcar que la información ha sido recogida
//            } catch (Exception e) {
//                e.printStackTrace();
//                velocidadInternetStr = "Error al obtener la velocidad de Internet.";
//            }
//        }
//
//        // Obtener USB conectados
//        leerUSB lectorUSB = new leerUSB();
//        try {
//            leerUSBStr = lectorUSB.obtenerAlmacenamiento();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Construir el mensaje final separado por comas
//
//        mensaje.append(cargaCPUStr).append(", ");
//        mensaje.append(usoRAMStr).append(", ");
//        mensaje.append(velocidadInternetStr).append(", ");
//        // mensaje.append(temperaturaCPUStr).append(", ");
//        mensaje.append(leerUSBStr);
//    }
//}
