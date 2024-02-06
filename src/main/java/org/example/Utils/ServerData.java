package org.example.Utils;

public class ServerData {

    private String cpuUsage;
    private String ramUsage;
    private String Storage;
    private String internetSpeed;

    public ServerData(String cpuUsage, String ramUsage, String usbStorage, String internetSpeed) {
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.Storage = usbStorage;
        this.internetSpeed = internetSpeed;
    }

    public ServerData() {

    }

    public String getCpuUsage() {
        return cpuUsage;
    }

    public String getRamUsage() {
        return ramUsage;
    }

    public String getUsbStorage() {
        return Storage;
    }

    public String getInternetSpeed() {
        return internetSpeed;
    }
}
