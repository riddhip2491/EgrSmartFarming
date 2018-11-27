package com.egr.smartfarming.model;


import org.hibernate.annotations.Generated;

import javax.persistence.*;

@Entity
@Table(name="current_market")
public class CurrentMarket {

    @Id
    @GeneratedValue
    @Column(name = "id",updatable = false, nullable = false)
    private long id;

    @Column(name= "timestamp")
    private long timestamp;

    @Column(name="state")
    private String state;

    @Column(name="district")
    private String district;

    @Column(name="market")
    private String market;

    @Column(name="commodity")
    private String commodity;

    @Column(name="variety")
    private String variety;

    @Column(name= "arrivaldate")
    private String arrivalDate;

    @Column(name = "minprice")
    private long minPrice;

    @Column(name="maxprice")
    private long maxPrice;

    @Column(name="modalprice")
    private long modalPrice;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(long minPrice) {
        this.minPrice = minPrice;
    }

    public long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public long getModalPrice() {
        return modalPrice;
    }

    public void setModalPrice(long modalPrice) {
        this.modalPrice = modalPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
