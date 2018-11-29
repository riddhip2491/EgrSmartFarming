package com.egr.smartfarming.service;

import com.egr.smartfarming.model.*;

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
import java.text.ParseException;
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

    @Value("${openweather.apikey}")
    private String weatherApiKey;

    @Value("${openweather.url}")
    private String weatherApi;

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

    public ArrayList<Decision> processFinalDecision(Decision decision, String latitude, String longitude){
        ArrayList<SeasonIndia> seasonIndiaList = seasonRepository.getAllSeasons();
        ArrayList<String> cropList = new ArrayList<>();
        ArrayList<Decision> decisionArrayList = new ArrayList<>();
        //TODO Process Season
        if (decision.getCropType() == null || decision.getCropType().equalsIgnoreCase("none"))
            processSeason(seasonIndiaList,decision);
        else{
            Object[] seasonMonths = (Object[])seasonRepository.getMonths(decision.getCropType());

            decision.setStartMonth((String)seasonMonths[0]);
            decision.setEndMonth((String)seasonMonths[1]);
        }
        int soilType = seasonRepository.getSoilType(decision.getSoilName());
        //TODO Process Location Temperature
        Temperature temperature = new Temperature();
        if (decision.getAvgTemperature()== -1){
             temperature = processLocationTemperature(decision,latitude,longitude);
        }
        else{
            if (decision.getAvgTemperature() == 20){
                temperature.setMinTemeperature(0);
                temperature.setMaxTemperature(20);
            }else if (decision.getAvgTemperature() == 22.5){
                temperature.setMinTemeperature(20);
                temperature.setMaxTemperature(22.5);
            }else if (decision.getAvgTemperature() == 25){
                temperature.setMinTemeperature(22.5);
                temperature.setMaxTemperature(25);
            } else if (decision.getAvgTemperature() == 27.5){
                temperature.setMinTemeperature(25);
                temperature.setMaxTemperature(27.5);
            }else if (decision.getAvgTemperature() > 27.5){
                temperature.setMinTemeperature(20);
                temperature.setMaxTemperature(45);
            }
        }
        //TODO Process Rainfall
        if (decision.getAvgRainfall() == 40) {
            decision.setMinRainfall(20);
            decision.setMaxRainfall(40);
        } else if (decision.getAvgRainfall() == 50) {
            decision.setMinRainfall(40);
            decision.setMaxRainfall(50);
        } else if (decision.getAvgRainfall() == 100) {
            decision.setMinRainfall(50);
            decision.setMaxRainfall(100);
        } else if (decision.getAvgRainfall() == 150) {
            decision.setMinRainfall(100);
            decision.setMaxRainfall(150);
        } else if (decision.getAvgRainfall() == 250) {
            decision.setMinRainfall(150);
            decision.setMaxRainfall(250);
        } else {
            decision.setMinRainfall(250);
            decision.setMaxRainfall(300);
        }
        if (decision.getpH() > -1 && decision.getEc() > -1){
            cropList = seasonRepository.getCropDecisionByAll(soilType, decision.getCropType(), temperature.getMinTemeperature(), temperature.getMaxTemperature(), decision.getMinRainfall(), decision.getMaxRainfall(),decision.getpH(),decision.getpH(),decision.getEc(),decision.getEc());
        }else if (decision.getpH() > -1){
            cropList = seasonRepository.getCropDecisionByPH(soilType, decision.getCropType(), temperature.getMinTemeperature(), temperature.getMaxTemperature(), decision.getMinRainfall(), decision.getMaxRainfall(),decision.getpH(),decision.getpH());
        }else if (decision.getEc() > -1){
            cropList = seasonRepository.getCropDecisionByEC(soilType, decision.getCropType(), temperature.getMinTemeperature(), temperature.getMaxTemperature(), decision.getMinRainfall(), decision.getMaxRainfall(),decision.getEc(),decision.getEc());
        }else {
           cropList = seasonRepository.processCropDecision(soilType, decision.getCropType(), temperature.getMinTemeperature(), temperature.getMaxTemperature(), decision.getMinRainfall(), decision.getMaxRainfall());
        }
        System.out.println("Crop List" + cropList.size());
        //TODO Current Market Value
        ArrayList<CurrentMarket> currentMarketList = new ArrayList<>();
        ArrayList<String> tmpList = new ArrayList<>();
       for (int i=0; i< cropList.size();i++){
           ArrayList<CurrentMarket> currentMarketoFCommodity = currentMarketRepository.findAllByCommodity(cropList.get(i));
           if(currentMarketoFCommodity.size()> 0){
                for (int j=0;i < cropList.size();i++){
                    currentMarketList.add(currentMarketoFCommodity.get(j));
                }
           }
       }
       if(currentMarketList.size()>0){
           long minPrice = currentMarketList.get(0).getMinPrice();
           for(int i =0; i < currentMarketList.size();i++){
               if(minPrice > currentMarketList.get(i).getMinPrice()){
                   if(tmpList.size()>0) {
                       tmpList.remove(tmpList.size()-1);
                   }
                   tmpList.add(currentMarketList.get(i).getCommodity());
               }
           }
       }
       if(tmpList.size() > 0){
           cropList = tmpList;
       }

       //End of Market value decision

        for (int i =0; i < cropList.size();i++){
            decision.setCropName(cropList.get(i));
            decisionArrayList.add(decision);
        }
        return decisionArrayList;
    }

    public ArrayList<Decision> processDecision(Decision decision, String latitude, String longitude){
        ArrayList<SeasonIndia> seasonIndiaList = seasonRepository.getAllSeasons();
        ArrayList<Decision> decisionArrayList = new ArrayList<>();

        String getDecision = new String();
        //Processing Season
        /*if(decision.getCropType() != null && !decision.getCropType().equalsIgnoreCase("none")){
            getDecision = "Your crop season will be " + decision.getCropType();
        }*/if (decision.getCropType() == null || decision.getCropType().equalsIgnoreCase("none"))
            processSeason(seasonIndiaList, decision);

        //Process Location //TODO Actual decision will be based on location
      //   String location = processLocation(latitude, longitude);

        //625733
            //TODO after getting location, below will be the method
       // ArrayList<String>getCropDecision= seasonRepository.getCropDecision(decision.getSoilName(), decision.getCropType(),location );
        ArrayList<String>getCropDecision= seasonRepository.getCropDecision(decision.getSoilName(), decision.getCropType() );
        /*if (getCropDecision == null)
            return "Please make different selection. No crop can be grown";
        //Processing Temperature
        if (decision.getAvgTemperature() >=0){
            ArrayList<String> getTempCropDec = new ArrayList<>();
           if (decision.getAvgTemperature()== 20){
               decision.setMinTemperature(0);
               decision.setMaxTemperature(20);
           } else  if (decision.getAvgTemperature()== 22.5){
               decision.setMinTemperature(20);
               decision.setMaxTemperature(23);
           }else  if (decision.getAvgTemperature()== 25){
               decision.setMinTemperature(22);
               decision.setMaxTemperature(25);
           }else  if (decision.getAvgTemperature()== 28){
               decision.setMinTemperature(25);
               decision.setMaxTemperature(28);
           }else {
               decision.setMinTemperature(28);
               decision.setMaxTemperature(40);
           }
            for(int i =0; i < getCropDecision.size();i++){
                Temperature tmpTemperature = new Temperature();
                ArrayList<Object> tmpList = seasonRepository.getCropTemperature(getCropDecision.get(i));
                Object[] objArr = (Object[]) tmpList.get(0);
                tmpTemperature.setAvgTemperature((Double) objArr[0]);
                tmpTemperature.setMinTemeperature((Double) objArr[1]);
                tmpTemperature.setMaxTemperature((Double)objArr[2]);
                if(decision.getMinTemperature() >= tmpTemperature.getMinTemeperature() && decision.getMaxTemperature() <= tmpTemperature.getMaxTemperature()){
                    getTempCropDec.add(getCropDecision.get(i));
                }
            }
            getCropDecision = getTempCropDec;
        }else */{
            getCropDecision = processTemperature(decision, latitude, longitude, getCropDecision);
        }
        if (getCropDecision == null)
            return null;
        //Process Rainfall
        getCropDecision = processRainfall(decision,getCropDecision);
        if (getCropDecision == null)
            return null;

        //Process pH
        if (decision.getpH() != -1)
            getCropDecision = processpH(decision, getCropDecision);

        //Process EC- Salt Tolerance
        if(decision.getEc() != -1)
            getCropDecision = processEC(decision, getCropDecision);

        //Process Current MArket Value
        ArrayList<String> finalDecision = new ArrayList<>();
        ArrayList<Long> minPrice = new ArrayList<>();
        for(int i=0; i< getCropDecision.size();i++){
            ArrayList<CurrentMarket> getCurrentMarketValue = currentMarketRepository.findAllByCommodity(getCropDecision.get(i));

           /* if(getCurrentMarketValue.size()==0) {
                return null;
            }
            else */if (getCurrentMarketValue.size()> 0){
                minPrice.add(getCurrentMarketValue.get(i).getMinPrice());
                if (i > 0){
                    if(minPrice.get(i-1) < minPrice.get(i)){
                        finalDecision.add(getCropDecision.get(i));
                    }
                }

            }
        }
        if (getCropDecision != null){
            for(int i=0;i < getCropDecision.size();i++){
                decision.setCropName(getCropDecision.get(i));
                decisionArrayList.add(decision);
                /*decisionArrayList.get(i).setMaxTemperature(decision.getMaxTemperature());
                decisionArrayList.get(i).setMinTemperature(decision.getMinTemperature());
                decisionArrayList.get(i).setEndMonth(decision.getEndMonth());
                decisionArrayList.get(i).setStartMonth(decision.getStartMonth());
                decisionArrayList.get(i).setMaxRainfall(decision.getMaxRainfall());
                decisionArrayList.get(i).setMinRainfall(decision.getMinRainfall());
                decisionArrayList.get(i).setCropType(decision.getCropType());
                decisionArrayList.get(i).setAvgRainfall(decision.getAvgRainfall());
                decisionArrayList.get(i).setAvgTemperature(decision.getAvgTemperature());
                decisionArrayList.get(i).setCropName(getCropDecision.get(i));
                decisionArrayList.get(i).setCropType(decision.getCropType());
                decisionArrayList.get(i).setpH(decision.getpH());
                decisionArrayList.get(i).setEc(decision.getEc());
                decisionArrayList.get(i).setSoilName(decision.getSoilName());*/
            }
        }
        String cropDec = new String();
//        for (int i=0; i< finalDecision.size();i++){
//            if (i == 0) {
//                getDecision = "Crop that can be grown is: ";
//            }
//                getDecision = cropDec + finalDecision.get(i) + ", ";
//
//                if(i+1 == finalDecision.size()){
//                    getDecision = "&nbsp;" + getDecision;
//                }
//        }

        //Get Market Value

        return decisionArrayList;
    }


    private void processSeason(ArrayList<SeasonIndia> seasonIndiaList,Decision decision){
        String getSeason= null;
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM");
        String monthName = simpleDateFormat.format(date);

        for (int i=0; i<seasonIndiaList.size();i++){
            SeasonIndia seasonIndia = seasonIndiaList.get(i);
            if(monthName.equalsIgnoreCase(seasonIndia.getStartMonth())){
                getSeason = "Your crop season will be: " + seasonIndia.getCropSeasonType();
                getSeason = getSeason + "&nbsp; Start month will be: " + seasonIndia.getStartMonth() +
                            " and End month will be: " + seasonIndia.getEndMonth();
                decision.setStartMonth(seasonIndia.getStartMonth());
                decision.setEndMonth(seasonIndia.getEndMonth());
                decision.setCropType(seasonIndia.getCropSeasonType());
                break;
            }else if (monthName.equalsIgnoreCase(seasonIndia.getEndMonth())){
                simpleDateFormat = new SimpleDateFormat("MMMM");
                String monthNo = simpleDateFormat.format(date.getMonth()+1);
                getSeason = "Your next crop season will be: " + monthNo;
                getSeason = getSeason + "&nbsp; Start month will be: " + seasonIndia.getStartMonth() +
                        " and End month will be: " + seasonIndia.getEndMonth();
                decision.setStartMonth(seasonIndia.getStartMonth());
                decision.setEndMonth(seasonIndia.getEndMonth());
                decision.setCropType(seasonIndia.getCropSeasonType());
                break;
            }else{
                if(monthName.equalsIgnoreCase("January") || monthName.equalsIgnoreCase("February") || monthName.equalsIgnoreCase("November") || monthName.equalsIgnoreCase("December")){
                    SeasonIndia tmpSeason = seasonRepository.findBySeason("March");
                    getSeason = "Your crop season will be: " + tmpSeason.getCropSeasonType();
                    getSeason = getSeason + "\n Start month will be: " + tmpSeason.getStartMonth() +
                            " and End month will be: " + tmpSeason.getEndMonth();
                    decision.setStartMonth(tmpSeason.getStartMonth());
                    decision.setEndMonth(tmpSeason.getEndMonth());
                    decision.setCropType(tmpSeason.getCropSeasonType());
                    break;
                }
                else if(monthName.equalsIgnoreCase("April") || monthName.equalsIgnoreCase("May")){
                    SeasonIndia tmpSeason = seasonRepository.findBySeason("July");
                    getSeason = "Your crop season will be: " + tmpSeason.getCropSeasonType();
                    getSeason = getSeason + "\n Start month will be: " + tmpSeason.getStartMonth() +
                            " and End month will be: " + tmpSeason.getEndMonth();
                    decision.setStartMonth(tmpSeason.getStartMonth());
                    decision.setEndMonth(tmpSeason.getEndMonth());
                    decision.setCropType(tmpSeason.getCropSeasonType());
                    break;
                }else if(monthName.equalsIgnoreCase("August") || monthName.equalsIgnoreCase("September")){
                    SeasonIndia tmpSeason = seasonRepository.findBySeason("July");
                    getSeason = "Your crop season will be: " + tmpSeason.getCropSeasonType();
                    getSeason = getSeason + "\n Start month will be: " + tmpSeason.getStartMonth() +
                            " and End month will be: " + tmpSeason.getEndMonth();
                    decision.setStartMonth(tmpSeason.getStartMonth());
                    decision.setEndMonth(tmpSeason.getEndMonth());
                    decision.setCropType(tmpSeason.getCropSeasonType());
                    break;
                }
            }

        }

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

    private Temperature processLocationTemperature(Decision decision, String latitude, String longitude){
        try {
            String uri = soilGrids + "lon=" + longitude + "&lat=" + latitude;
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            Object object = new JSONParser().parse(result);
            JSONObject jsonObject = (JSONObject) object;
            JSONObject properties = (JSONObject) jsonObject.get("properties");
            System.out.println("Temperature Daytime: " + properties.get("TMDMOD_2011"));
            //Average Daytime Temperature annually
            JSONObject tempDaytime = (JSONObject) properties.get("TMDMOD_2011");

            JSONObject months = (JSONObject) tempDaytime.get("M");

            ArrayList<Long> monthsTemp = new ArrayList<Long>();
            if (decision.getCropType().equalsIgnoreCase("Kharif")) {
                monthsTemp.add((long) months.get("Jul"));
                monthsTemp.add((long) months.get("Aug"));
                monthsTemp.add((long) months.get("Sep"));
                monthsTemp.add((long) months.get("Oct"));
            } else if (decision.getCropType().equalsIgnoreCase("Rabi")) {
                monthsTemp.add((long) months.get("Oct"));
                monthsTemp.add((long) months.get("Nov"));
                monthsTemp.add((long) months.get("Jan"));
                monthsTemp.add((long) months.get("Dec"));
                monthsTemp.add((long) months.get("Feb"));
                monthsTemp.add((long) months.get("Mar"));
            } else if (decision.getCropType().equalsIgnoreCase("Summer")) {
                monthsTemp.add((long) months.get("Mar"));
                monthsTemp.add((long) months.get("Apr"));
                monthsTemp.add((long) months.get("May"));
                monthsTemp.add((long) months.get("Jun"));
            }
            long sumTotal = 0;
            long minDay = monthsTemp.get(0);
            long maxDay = monthsTemp.get(0);
            for (int i = 0; i < monthsTemp.size(); i++) {
                if (minDay > monthsTemp.get(i))
                    minDay = monthsTemp.get(i);
                if (maxDay < monthsTemp.get(i))
                    maxDay = monthsTemp.get(i);
                sumTotal = sumTotal + monthsTemp.get(i);
            }
            long avgDTemp = sumTotal / monthsTemp.size();
            System.out.println("Avg Day Time Temp: " + avgDTemp);
            System.out.println("Min Day Temp: " + minDay + "\n max Day Temp:" + maxDay);

            //Average Nighttime Temperature annually
            JSONObject tempNighttime = (JSONObject) properties.get("TMNMOD_2011");
            //System.out.println("Temperature: " + tempDaytime.get("M"));
            JSONObject monthsN = (JSONObject) tempNighttime.get("M");
            ArrayList<Long> monthsNTemp = new ArrayList<Long>();
            if (decision.getCropType().equalsIgnoreCase("Kharif")) {
                monthsNTemp.add((long) monthsN.get("Jul"));
                monthsNTemp.add((long) monthsN.get("Aug"));
                monthsNTemp.add((long) monthsN.get("Sep"));
                monthsNTemp.add((long) monthsN.get("Oct"));
            } else if (decision.getCropType().equalsIgnoreCase("Rabi")) {
                monthsNTemp.add((long) monthsN.get("Oct"));
                monthsNTemp.add((long) monthsN.get("Nov"));
                monthsNTemp.add((long) monthsN.get("Dec"));
                monthsNTemp.add((long) monthsN.get("Jan"));
                monthsNTemp.add((long) monthsN.get("Feb"));
                monthsNTemp.add((long) monthsN.get("Feb"));
            } else if (decision.getCropType().equalsIgnoreCase("Summer")) {
                monthsNTemp.add((long) monthsN.get("Mar"));
                monthsNTemp.add((long) monthsN.get("Apr"));
                monthsNTemp.add((long) monthsN.get("May"));
                monthsNTemp.add((long) monthsN.get("Jun"));
            }
            long sumNightTotal = 0;
            long minNight = monthsNTemp.get(0);
            long maxNight = monthsNTemp.get(0);
            for (int i = 0; i < monthsNTemp.size(); i++) {
                if (minNight > monthsNTemp.get(i))
                    minNight = monthsNTemp.get(i);
                if (maxNight < monthsNTemp.get(i))
                    maxNight = monthsNTemp.get(i);
                sumNightTotal = sumNightTotal + monthsNTemp.get(i);
            }

            long avgNTemp = sumNightTotal / monthsNTemp.size();
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

            temperature.setAvgTemperature((avgDTemp + avgNTemp) / 2);
            return temperature;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private String processLocation(String latitide, String longitude) {
        final String uri = weatherApi + "?lat=" + latitide + "&units=metric" + "&lon=" + longitude + "&APPID=" + weatherApiKey;
        RestService restService = new RestService();
        return "Madhya Pradesh";//restService.getLocationName(uri, latitide, longitude)
    }

    private ArrayList<String> processRainfall(Decision decision, ArrayList<String> getCropDecision){
        try {
            ArrayList<String> getRainfallDecision = new ArrayList<>();
            if (decision.getAvgRainfall() == 40) {
                decision.setMinRainfall(20);
                decision.setMinRainfall(40);
            } else if (decision.getAvgRainfall() == 50) {
                decision.setMinRainfall(40);
                decision.setMinRainfall(50);
            } else if (decision.getAvgRainfall() == 100) {
                decision.setMinRainfall(50);
                decision.setMinRainfall(100);
            } else if (decision.getAvgRainfall() == 150) {
                decision.setMinRainfall(100);
                decision.setMinRainfall(150);
            } else if (decision.getAvgRainfall() == 250) {
                decision.setMinRainfall(150);
                decision.setMaxRainfall(250);
            } else {
                decision.setMinRainfall(250);
                decision.setMinRainfall(300);
            }
            for (int i = 0; i < getCropDecision.size(); i++) {
                CropRain cropRain = new CropRain();
                ArrayList<Object> tmpList = seasonRepository.getCropRainfall(getCropDecision.get(i));
                Object[] objArr = (Object[]) tmpList.get(0);
                cropRain.setAvgRainfall((Float) objArr[0]);
                cropRain.setMinRainfall((Float) objArr[1]);
                cropRain.setMaxRainfall((Float) objArr[2]);

                if (decision.getMinRainfall() >= cropRain.getMinRainfall() && decision.getMaxRainfall() <= cropRain.getMaxRainfall()) {
                    getRainfallDecision.add(getCropDecision.get(i));
                }
            }
            return getRainfallDecision;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private ArrayList<String> processpH(Decision decision, ArrayList<String> getCropDecision){
        try {
            ArrayList<String> getpHDecision = new ArrayList<>();
            for (int i = 0; i < getCropDecision.size(); i++) {
                CropPH cropPH = new CropPH();
                ArrayList<Object> tmpList = seasonRepository.getCropBypH(getCropDecision.get(i));
                Object[] objArr = (Object[]) tmpList.get(0);
                cropPH.setMinpH((Float) objArr[1]);
                cropPH.setMaxpH((Float) objArr[2]);

                if (decision.getpH() > cropPH.getMinpH() && decision.getpH() <= cropPH.getMaxpH())
                    getpHDecision.add(getCropDecision.get(i));
            }
            return getpHDecision;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> processEC(Decision decision, ArrayList<String> getCropDecision){
        try {
            ArrayList<String> getECDecision = new ArrayList<>();
            for (int i = 0; i < getCropDecision.size(); i++) {
                CropEC cropEC = new CropEC();
                ArrayList<Object> tmpList = seasonRepository.getCropByEC(getCropDecision.get(i));
                Object[] objArr = (Object[]) tmpList.get(0);
                cropEC.setMinEc((Float) objArr[1]);
                cropEC.setMaxEC((Float) objArr[2]);

                if (decision.getpH() > cropEC.getMinEc() && decision.getpH() <= cropEC.getMaxEC())
                    getECDecision.add(getCropDecision.get(i));
            }
            return getECDecision;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
