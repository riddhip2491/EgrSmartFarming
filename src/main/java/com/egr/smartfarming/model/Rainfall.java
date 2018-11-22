package com.egr.smartfarming.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Rainfall")
public class Rainfall {

    @Id
    @Column(name="currenttimeinmillis")
    private long currentTimeinMillis;

    @Column(name="serialnumber")
    private String serialNumber;

    @Column(name="batteryvoltage")
    private String batteryVoltage;

    @Column(name="clicktype")
    private String clickType;

    public long getCurrentTimeinMillis() {
        return currentTimeinMillis;
    }

    public void setCurrentTimeinMillis(long currentTimeinMillis) {
        this.currentTimeinMillis = currentTimeinMillis;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(String batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public String getClickType() {
        return clickType;
    }

    public void setClickType(String clickType) {
        this.clickType = clickType;
    }
}
