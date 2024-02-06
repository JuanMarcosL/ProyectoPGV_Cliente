package org.example.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class Connection {
    private String serverAlias;
    private String ip;
    private int port;

    private Thread thread = null;
    private Socket socket;
    private final Object lock = new Object(); // Objeto para sincronizar el hilo que recibe datos
    private boolean connected = false;

    public boolean isReceivingData() {
        return receivingData;
    }

    public void setReceivingData(boolean receivingData) {
        this.receivingData = receivingData;
    }

    //private AtomicReference<String> lastMessage = new AtomicReference<>("");
    private boolean receivingData = false;
    private static String message;


    public Connection(String alias, String ip, int port) {
        this.serverAlias = alias;
        this.ip = ip;
        this.port = port;
    }

    public Socket createConexion() {
        try {
            socket = new Socket(ip, port);
            OpenConnections.addConnection(this);
            System.out.println("Conexión establecida");
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void receiveData() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                message = serverResponse;
                System.out.println("En el metodo ReceiveData" +message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerAlias() {
        return serverAlias;
    }

    public void setServerAlias(String serverAlias) {
        this.serverAlias = serverAlias;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Object getLock() {
        return lock;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public static String getMessage() {
        return message;
    }

    public static void setMessage(String message) {
        Connection.message = message;
    }
}