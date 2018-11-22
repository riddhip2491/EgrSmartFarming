package com.egr.smartfarming.service;

import com.egr.smartfarming.model.Rainfall;
import com.egr.smartfarming.model.Weather;
import com.egr.smartfarming.repository.RainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

@Service("rainfallService")
public class RainfallService {

    RainRepository rainRepository;
    @Autowired
    public RainfallService(RainRepository rainRepository){
        this.rainRepository = rainRepository;
    }

    public void saveRainData(Rainfall rainfall){
        rainRepository.save(rainfall);
    }


    public Rainfall getLatestRainData(){
        return rainRepository.getLatestRainData();
    }
}
