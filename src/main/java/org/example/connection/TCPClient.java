package org.example.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TCPClient {
    private List<Socket> sockets = new ArrayList<>();
    private Map<String, String> servers = new HashMap<>();


    private static String lastMessage = "";


    String alias = "";
    String ip = "";
    int port = 0;


    public TCPClient(String alias, String ip, int port) {
        this.alias = alias;
        this.ip = ip;
        this.port = port;
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
            servers.put(key, alias);
            sockets.add(socket);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readLine();
                        lastMessage = message; // Guarda el último mensaje recibido

                        System.out.println("En tcpClient" + message); //recibo la RAM
//                        System.out.println("En tcpClient lastMessage" + lastMessage); //recibo la RAM
                    }
                } catch (IOException ex) {
                    System.out.println("El servidor se ha desconectado.");
                    try {
                        socket.close();
                    } catch (IOException ex1) {
                        ex1.printStackTrace();
                    }
                    sockets.remove(socket);
                    servers.remove(key);
                }
            }).start();

        } catch (IOException ex) {
            System.out.println("No se ha podido conectar al servidor.");
        }
    }

    public static String getLastMessage() {
        return lastMessage;
    }


}