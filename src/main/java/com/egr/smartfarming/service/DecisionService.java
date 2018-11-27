package com.egr.smartfarming.service;

import com.egr.smartfarming.model.CurrentMarket;
import com.egr.smartfarming.model.Decision;
import com.egr.smartfarming.model.SeasonIndia;

import com.egr.smartfarming.model.Temperature;
import com.egr.smartfarming.repository.CurrentMarketRepository;
import com.egr.smartfarming.repository.SeasonRepository;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.management.Query;
import javax.persistence.criteria.CriteriaBuilder;
import java.awt.event.TextEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.Iterator;


@Service("DecisionService")
public class DecisionService {


    @Autowired
    private SeasonRepository seasonRepository;
    @Autowired
    private CurrentMarketRepository currentMarketRepository;

    private Temperature locationTemp;

    @Value("${soilgrids.api}")
    private String soilGrids;

    private int counter;
    /*@Autowired
    private DecisionRepository decisionRepository;*/

    public DecisionService(SeasonRepository seasonRepository, CurrentMarketRepository currentMarketRepository){
        this.seasonRepository = seasonRepository;
        this.currentMarketRepository = currentMarketRepository;


    }

    public String processDecision(Decision decision, String latitude, String longitude){
        ArrayList<SeasonIndia> seasonIndiaList = seasonRepository.getAllSeasons();
        //Processing Season
        String getDecision = processSeason(seasonIndiaList, decision);
        //625733

        ArrayList<String> getCropDecision= seasonRepository.getCropDecision(decision.getSoilName(), decision.getCropType());

        //Processing Temperature
        getCropDecision = processTemperature(decision, latitude, longitude,getCropDecision);

        for(int i=0; i< getCropDecision.size();i++){
            ArrayList<CurrentMarket> getCurrentMarketValue = currentMarketRepository.findAllByCommodity(getCropDecision.get(i));
            //Temperature temperature = seasonRepository.getCropTemperature(getCropDecision.get(i));
           /* if((locationTemp.getMinTemeperature() >= temperature.getMinTemeperature()) && locationTemp.getMaxTemperature()<= temperature.getMaxTemperature()){

            }*/
            if(getCurrentMarketValue.size()==0) {
                getDecision = "Crop that can be grown is: " + getCropDecision.get(i) + "\n " + getDecision;
            }
            else

                //TODO write code if the current market value is found
                getDecision = "Crop that can be grown is: " + getCropDecision.get(i) + "\n " + getDecision;
        }

        //Get Market Value

        return getDecision;
    }


    private String processSeason(ArrayList<SeasonIndia> seasonIndiaList,Decision decision){
        String getSeason= null;
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM");
        String monthName = simpleDateFormat.format(date);

        System.out.println();

        for (int i=0; i<seasonIndiaList.size();i++){
            SeasonIndia seasonIndia = seasonIndiaList.get(i);
            if(monthName.equalsIgnoreCase(seasonIndia.getStartMonth())){
                getSeason = "Your crop season will be: " + seasonIndia.getCropSeasonType();
                getSeason = getSeason + "\n Start monh will be: " + seasonIndia.getStartMonth() +
                            " and End month will be: " + seasonIndia.getEndMonth();
                decision.setCropType(seasonIndia.getCropSeasonType());
                break;
            }else if (monthName.equalsIgnoreCase(seasonIndia.getEndMonth())){
                simpleDateFormat = new SimpleDateFormat("MMMM");
                String monthNo = simpleDateFormat.format(date.getMonth()+1);
                getSeason = "Your next crop season will be: " + monthNo;
                getSeason = getSeason + "\n Start month will be: " + seasonIndia.getStartMonth() +
                        " and End month will be: " + seasonIndia.getEndMonth();
                decision.setCropType(seasonIndia.getCropSeasonType());
                break;
            }else{
                if(monthName.equalsIgnoreCase("January") || monthName.equalsIgnoreCase("February") || monthName.equalsIgnoreCase("November") || monthName.equalsIgnoreCase("December")){
                    SeasonIndia tmpSeason = seasonRepository.findBySeason("March");
                    getSeason = "Your crop season will be: " + tmpSeason.getCropSeasonType();
                    getSeason = getSeason + "\n Start month will be: " + tmpSeason.getStartMonth() +
                            " and End month will be: " + tmpSeason.getEndMonth();
                    decision.setCropType(tmpSeason.getCropSeasonType());
                    break;
                }
                else if(monthName.equalsIgnoreCase("April") || monthName.equalsIgnoreCase("May")){
                    SeasonIndia tmpSeason = seasonRepository.findBySeason("July");
                    getSeason = "Your crop season will be: " + tmpSeason.getCropSeasonType();
                    getSeason = getSeason + "\n Start month will be: " + tmpSeason.getStartMonth() +
                            " and End month will be: " + tmpSeason.getEndMonth();
                    decision.setCropType(tmpSeason.getCropSeasonType());
                    break;
                }else if(monthName.equalsIgnoreCase("August") || monthName.equalsIgnoreCase("September")){
                    SeasonIndia tmpSeason = seasonRepository.findBySeason("July");
                    getSeason = "Your crop season will be: " + tmpSeason.getCropSeasonType();
                    getSeason = getSeason + "\n Start month will be: " + tmpSeason.getStartMonth() +
                            " and End month will be: " + tmpSeason.getEndMonth();
                    decision.setCropType(tmpSeason.getCropSeasonType());
                    break;
                }
            }

        }
        return getSeason;
    }

    private ArrayList<String> processTemperature(Decision decision, String latitude, String longitude, ArrayList<String> getCropDecision){
        try {
            ArrayList<String> getTempCropDec = new ArrayList<String>();
            String uri = soilGrids + "lon=" + longitude + "&lat=" + latitude;
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            Object object = new JSONParser().parse(result);
            JSONObject jsonObject = (JSONObject) object;
            JSONObject properties = (JSONObject) jsonObject.get("properties");
            System.out.println("Temperature Daytime: " + properties.get("TMDMOD_2011"));
            //Average Daytime Temperature annually
            JSONObject tempDaytime = (JSONObject)properties.get("TMDMOD_2011");

            JSONObject months = (JSONObject)tempDaytime.get("M");

            ArrayList<Long> monthsTemp = new ArrayList<Long>();
            if(decision.getCropType().equalsIgnoreCase("Kharif")){
                monthsTemp.add((long)months.get("Jul"));
                monthsTemp.add((long)months.get("Aug"));
                monthsTemp.add((long)months.get("Sep"));
                monthsTemp.add((long)months.get("Oct"));
            } else if (decision.getCropType().equalsIgnoreCase("Rabi")){
                monthsTemp.add((long)months.get("Oct"));
                monthsTemp.add((long)months.get("Nov"));
                monthsTemp.add((long)months.get("Jan"));
                monthsTemp.add((long)months.get("Dec"));
                monthsTemp.add((long)months.get("Feb"));
                monthsTemp.add((long)months.get("Mar"));
            }else if (decision.getCropType().equalsIgnoreCase("Summer")){
                monthsTemp.add((long)months.get("Mar"));
                monthsTemp.add((long)months.get("Apr"));
                monthsTemp.add((long)months.get("May"));
                monthsTemp.add((long)months.get("Jun"));
            }
            long sumTotal =0;
            long minDay = monthsTemp.get(0);
            long maxDay= monthsTemp.get(0);
            for(int i=0; i< monthsTemp.size();i++){
                if(minDay > monthsTemp.get(i))
                    minDay = monthsTemp.get(i);
                if(maxDay < monthsTemp.get(i))
                    maxDay = monthsTemp.get(i);
                sumTotal = sumTotal+ monthsTemp.get(i);
            }
            long avgDTemp = sumTotal /monthsTemp.size();
            System.out.println("Avg Day Time Temp: " + avgDTemp);
            System.out.println("Min Day Temp: " + minDay + "\n max Day Temp:" + maxDay);

            //Average Nighttime Temperature annually
            JSONObject tempNighttime = (JSONObject)properties.get("TMNMOD_2011");
            //System.out.println("Temperature: " + tempDaytime.get("M"));
            JSONObject monthsN = (JSONObject)tempNighttime.get("M");
            ArrayList<Long> monthsNTemp = new ArrayList<Long>();
            if(decision.getCropType().equalsIgnoreCase("Kharif")){
                monthsNTemp.add((long)monthsN.get("Jul"));
                monthsNTemp.add((long)monthsN.get("Aug"));
                monthsNTemp.add((long)monthsN.get("Sep"));
                monthsNTemp.add((long)monthsN.get("Oct"));
            } else if(decision.getCropType().equalsIgnoreCase("Rabi")){
                monthsNTemp.add((long)monthsN.get("Oct"));
                monthsNTemp.add((long)monthsN.get("Nov"));
                monthsNTemp.add((long)monthsN.get("Dec"));
                monthsNTemp.add((long)monthsN.get("Jan"));
                monthsNTemp.add((long)monthsN.get("Feb"));
                monthsNTemp.add((long)monthsN.get("Feb"));
            } else if (decision.getCropType().equalsIgnoreCase("Summer")){
                monthsNTemp.add((long)monthsN.get("Mar"));
                monthsNTemp.add((long)monthsN.get("Apr"));
                monthsNTemp.add((long)monthsN.get("May"));
                monthsNTemp.add((long)monthsN.get("Jun"));
            }
            long sumNightTotal =0;
            long minNight=monthsNTemp.get(0);
            long maxNight=monthsNTemp.get(0);
            for(int i=0; i< monthsNTemp.size();i++){
                if(minNight > monthsNTemp.get(i))
                    minNight = monthsNTemp.get(i);
                if(maxNight < monthsNTemp.get(i))
                    maxNight = monthsNTemp.get(i);
                sumNightTotal = sumNightTotal+ monthsNTemp.get(i);
            }

            long avgNTemp = sumNightTotal /monthsNTemp.size();
            System.out.println("Avg Day Time Temp: " + avgNTemp);
            System.out.println("Min Night Temp: " + minNight + "\n max Night Temp:" + maxNight);


            Temperature temperature = new Temperature();
           /* if (minDay > minNight)
                temperature.setMinTemeperature(minNight);
            else*/
                temperature.setMinTemeperature(minDay);

            /*if(maxDay < maxNight )
                temperature.setMaxTemperature(maxNight);
            else*/
                temperature.setMaxTemperature(maxDay);

            temperature.setAvgTemperature((avgDTemp+avgNTemp)/2);


        for(int i =0; i < getCropDecision.size();i++){
            Temperature tmpTemperature = new Temperature();
            ArrayList<Object> tmpList = seasonRepository.getCropTemperature(getCropDecision.get(i));
                    Object[] objArr = (Object[]) tmpList.get(0);
                    tmpTemperature.setAvgTemperature((Double) objArr[0]);
            tmpTemperature.setMinTemeperature((Double) objArr[1]);
            tmpTemperature.setMaxTemperature((Double)objArr[2]);
            if(temperature.getMinTemeperature() >= tmpTemperature.getMinTemeperature() && temperature.getMaxTemperature() <= tmpTemperature.getMaxTemperature()){
                getTempCropDec.add(getCropDecision.get(i));
            }
        }
            return getTempCropDec;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
