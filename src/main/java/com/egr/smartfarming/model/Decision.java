package com.egr.smartfarming.model;

public class Decision {

//private String username;
    private String cropName;

    private String cropType;

    private String soilName;

    private double avgTemperature;

    private double minTemperature;

    private double maxTemperature;

    private float avgRainfall;

    private float minRainfall;

    private float maxRainfall;

    private String startMonth;

    private String endMonth;

    private float pH;

    private float ec;

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

    public double getAvgTemperature() {
        return avgTemperature;
    }

    public void setAvgTemperature(double avgTemperature) {
        this.avgTemperature = avgTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public float getAvgRainfall() {
        return avgRainfall;
    }

    public void setAvgRainfall(float avgRainfall) {
        this.avgRainfall = avgRainfall;
    }

    public double getMinRainfall() {
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

    public float getpH() {
        return pH;
    }

    public void setpH(float pH) {
        this.pH = pH;
    }

    public float getEc() {
        return ec;
    }

    public void setEc(float ec) {
        this.ec = ec;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public String getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(String endMonth) {
        this.endMonth = endMonth;
    }
}
