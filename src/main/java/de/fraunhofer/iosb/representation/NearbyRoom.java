package de.fraunhofer.iosb.representation;

import java.io.Serializable;

/**
 *
 * @author Viseslav Sako
 */
public class NearbyRoom implements Serializable
{
    private double distance;

    private String id;
    private String proximityUUID;
    private String minor;
    private String major;  
    

    public NearbyRoom() {
    }

    public NearbyRoom(double distance, String id) {
        this.distance = distance;
        this.id = id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUUID() {
        return proximityUUID;
    }

    public void setProximityUUID(String proximityUUID) {
        this.proximityUUID = proximityUUID;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
