package org.example.connection;

import java.util.ArrayList;
import java.util.List;

public class OpenConnections {
    private static List<Connection> connections = new ArrayList<>();

    public static List<Connection> getConnections() {
        return connections;
    }

    public static void addConnection(Connection connection) {
        connections.add(connection);
    }
}