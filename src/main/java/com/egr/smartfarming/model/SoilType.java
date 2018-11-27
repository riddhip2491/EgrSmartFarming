package com.egr.smartfarming.model;

import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;



@Entity
@Table(name="soiltype")
public class SoilType {

    @Id
    @Column(name="soil_id")
    private int soilid ;

    @Column(name="soil_name")
    private String type;

    public int getSoilid() {
        return soilid;
    }

    public String getType() {
        return type;
    }

    public void setSoilid(int soilid) {
        this.soilid = soilid;
    }

    public void setType(String type) {
        this.type = type;
    }
}
