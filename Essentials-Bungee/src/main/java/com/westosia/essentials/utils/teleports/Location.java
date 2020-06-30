package com.westosia.essentials.utils.teleports;

public class Location {

    private String server;
    private double x, y, z;

    public Location(String server, double x, double y, double z) {
        this.server = server;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getServer() {
        return server;
    }

    public String toString() {
        return server + "|" +
                x + "|" +
                y + "|" +
                z;
    }
}
