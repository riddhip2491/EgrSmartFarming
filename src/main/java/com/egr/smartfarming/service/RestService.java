package com.egr.smartfarming.service;

import com.egr.smartfarming.model.Weather;

import com.egr.smartfarming.repository.UserRepository;
import com.egr.smartfarming.repository.WeatherRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;


public class RestService {

    private Double currentTemp;
    private String weatherDesc;

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    public RestService(){

    }

    @Autowired
    public RestService(WeatherRepository weatherRepository) {

        this.weatherRepository = weatherRepository;
    }
    public Weather getLocationObject(String uri, Weather weatherObj) {
        try {

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);
            Object object = new JSONParser().parse(result);
            JSONObject jsonObject = (JSONObject) object;
            //Get Current temperature from main object
            JSONObject main = (JSONObject) jsonObject.get("main");
            currentTemp = (Double) main.get("temp");
            //Get Weather Descriptpion
            JSONArray weather = (JSONArray) jsonObject.get("weather");
            Iterator iterator = weather.iterator();
            while (iterator.hasNext()) {
                Object nextObj = iterator.next();
                JSONObject jsonObject1 = (JSONObject) nextObj;
                    weatherDesc = (String) jsonObject1.get("description");
            }

            weatherObj.setCurrenttimeinmillis(System.currentTimeMillis());

            weatherObj.setCurrentTemperature(currentTemp);
            weatherObj.setDescription(weatherDesc);
           // saveWeather(weatherObj);
        }catch (Exception e){
            e.printStackTrace();
        }
        return weatherObj;
    }

    public void saveWeather(Weather weather) {
        weatherRepository.save(weather);
    }
}
