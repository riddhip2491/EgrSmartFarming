package com.egr.smartfarming.model;

public class Decision {

//private String username;
    private String cropName;

    private String cropType;

    private String soilName;

    private float avgTemperature;

    private float minTemperature;

    private float maxTemperature;

    private float avgRainfall;

    private float minRainfall;

    private float maxRainfall;

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public String getSoilName() {
        return soilName;
    }

    public void setSoilName(String soilName) {
        this.soilName = soilName;
    }

    public float getAvgTemperature() {
        return avgTemperature;
    }

    public void setAvgTemperature(float avgTemperature) {
        this.avgTemperature = avgTemperature;
    }

    public float getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(float minTemperature) {
        this.minTemperature = minTemperature;
    }

    public float getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public float getAvgRainfall() {
        return avgRainfall;
    }

    public void setAvgRainfall(float avgRainfall) {
        this.avgRainfall = avgRainfall;
    }

    public float getMinRainfall() {
        return minRainfall;
    }

    public void setMinRainfall(float minRainfall) {
        this.minRainfall = minRainfall;
    }

    public float getMaxRainfall() {
        return maxRainfall;
    }

    public void setMaxRainfall(float maxRainfall) {
        this.maxRainfall = maxRainfall;
    }

 /*   public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }*/
}
