package cn.hycer.carpetbotmanager.model;

import com.google.gson.annotations.SerializedName;

public class BotPreset {

    private String name;

    private String description;

    private String dimension;

    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    @SerializedName("look_x")
    private double lookX;

    @SerializedName("look_y")
    private double lookY;

    @SerializedName("look_z")
    private double lookZ;

    public BotPreset() {}

    public BotPreset(String name, String description, String dimension, double x, double y, double z,
                     float yaw, float pitch, double lookX, double lookY, double lookZ) {
        this.name = name;
        this.description = description;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.lookX = lookX;
        this.lookY = lookY;
        this.lookZ = lookZ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public double getLookX() {
        return lookX;
    }

    public void setLookX(double lookX) {
        this.lookX = lookX;
    }

    public double getLookY() {
        return lookY;
    }

    public void setLookY(double lookY) {
        this.lookY = lookY;
    }

    public double getLookZ() {
        return lookZ;
    }

    public void setLookZ(double lookZ) {
        this.lookZ = lookZ;
    }
}
