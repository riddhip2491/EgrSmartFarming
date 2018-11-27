package com.egr.smartfarming.model;

import javax.persistence.*;

@Entity
    @Table(name="seasonindia")
public class SeasonIndia {


    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="seasonid")
    private int seasonId;

    @Id
    @Column(name = "season")
    private String season;

    @Column(name="cropseason")
    private String cropSeasonType;

    @Column(name = "startmonth")
    private String startMonth;

    @Column(name="endmonth")
    private String endMonth;

    public int getSeasonId() {
        return seasonId;
    }

    public int setSeasonId() {
        return setSeasonId();
    }

    public void setSeasonId(int seasonId) {this.seasonId = seasonId; }
    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getCropSeasonType() {
        return cropSeasonType;
    }

    public void setCropSeasonType(String cropSeasonType) {
        this.cropSeasonType = cropSeasonType;
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
