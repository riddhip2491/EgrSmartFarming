package com.egr.smartfarming.model;

import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "weather")
public class Weather {

    @Id
    @Column(name = "currenttimeinmillis")
    @Type(type="long")
    private long currenttimeinmillis;

    @Column(name = "latitude")
    @Type(type="float")
    private float latitude;

    @Type(type="float")
    @Column(name="longitude")
    private float longitude;

    @Type(type="double")
    @Column(name="currenttemperature")
    private double currentTemperature;

    @Column(name="description")
    private String description;

    public long getCurrenttimeinmillis() {
        return currenttimeinmillis;
    }

    public void setCurrenttimeinmillis(long currenttimeinmillis) {
        this.currenttimeinmillis = currenttimeinmillis;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString(){
        return "Latitude is " + getLatitude() + " and Longitude is " + getLongitude();
    }

}

