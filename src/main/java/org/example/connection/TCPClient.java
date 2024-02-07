package org.example.connection;

import org.example.model.ServerInfo;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class TCPClient {
    private Map<String, ServerInfo> servers = new HashMap<>();
    private static AtomicReference<String> lastMessage = new AtomicReference<>("");
    private static Map<String, String> serverMessages = new HashMap<>(); // Nuevo campo para el mapa

    public TCPClient(String alias, String ip, int port) {
        connectToServer(alias, ip, port);
    }

    private void connectToServer(String alias, String host, int port) {
        String key = host + ":" + port;
        if (servers.containsKey(key)) {
            System.out.println("Esta dirección IP y puerto ya están en uso.");
            return;
        }

        try {
            Socket socket = new Socket(host, port);
            servers.put(key, new ServerInfo(alias, socket));

            Scanner in = new Scanner(socket.getInputStream());

            new Thread(() -> {
                try {
                    while (in.hasNextLine()) {
                        String message = in.nextLine();
                        lastMessage.set(message);
                        serverMessages.put(socket.getInetAddress().getHostAddress(), message); // Almacenar el mensaje asociado a la dirección IP


                    }
                } catch (Exception ex) {
                    System.out.println("El servidor se ha desconectado.");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    servers.remove(key);
                }
            }).start();

        } catch (IOException ex) {
            System.out.println("No se ha podido conectar al servidor.");
        }
    }

    public static void removeServerMessage(String ipAddress) {
        serverMessages.remove(ipAddress);
    }

    public static String getLastMessage() {
        return lastMessage.get();
    }

    // Nuevo método para obtener el mensaje asociado a una dirección MAC
//    public String getMessageForMac(String macAddress) {
//        return serverMessages.get(macAddress);
//    }

    public static Map<String, String> getServerMessages() {
        return serverMessages;
    }


}
