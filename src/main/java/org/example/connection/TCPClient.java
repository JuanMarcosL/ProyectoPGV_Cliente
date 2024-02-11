package org.example.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Esta clase se utiliza para establecer una conexión TCP con un servidor y manejar los mensajes recibidos.
 */
public class TCPClient {
    private static AtomicReference<String> lastMessage = new AtomicReference<>("");
    private static Map<String, String> serverMessages = new HashMap<>();

    /**
     * Constructor de la clase TCPClient.
     * @param alias Alias del cliente.
     * @param ip Dirección IP del servidor.
     * @param port Puerto del servidor.
     */
    public TCPClient(String alias, String ip, int port) {
        connectToServer(alias, ip, port);
    }

    /**
     * Este método se utiliza para conectar al servidor y manejar los mensajes recibidos.
     * @param alias Alias del cliente.
     * @param host Dirección IP del servidor.
     * @param port Puerto del servidor.
     */
    private void connectToServer(String alias, String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            Scanner in = new Scanner(socket.getInputStream());

            new Thread(() -> {
                try {
                    while (in.hasNextLine()) {
                        String message = in.nextLine();
                        lastMessage.set(message);
                        serverMessages.put(socket.getInetAddress().getHostAddress(), message);
                    }
                } catch (Exception ex) {
                    System.out.println("El servidor se ha desconectado.");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException ex) {
            System.out.println("No se ha podido conectar al servidor.");
        }
    }

    /**
     * Este método se utiliza para eliminar el mensaje del servidor asociado a la dirección IP dada.
     * @param ipAddress Dirección IP del servidor.
     */
    public static void removeServerMessage(String ipAddress) {
        serverMessages.remove(ipAddress);
    }

    /**
     * Este método se utiliza para obtener el último mensaje recibido.
     * @return El último mensaje recibido.
     */
    public static String getLastMessage() {
        return lastMessage.get();
    }

    /**
     * Este método se utiliza para obtener todos los mensajes del servidor.
     * @return Un mapa que contiene los mensajes del servidor asociados a sus respectivas direcciones IP.
     */
    public static Map<String, String> getServerMessages() {
        return serverMessages;
    }
}