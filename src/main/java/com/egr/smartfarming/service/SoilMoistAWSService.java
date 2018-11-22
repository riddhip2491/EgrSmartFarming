package com.egr.smartfarming.service;


import com.egr.smartfarming.model.SoilMoisture;
import com.egr.smartfarming.repository.SoilmoistRepository;
import org.springframework.stereotype.Service;

@Service("SoilMoistAWSService")
public class SoilMoistAWSService {
    SoilmoistRepository soilmoistRepository;

    public  SoilMoistAWSService(SoilmoistRepository soilmoistRepository){
        this.soilmoistRepository = soilmoistRepository;
    }

    public void saveSoilMoistData(SoilMoisture soilMoisture){
        soilmoistRepository.save(soilMoisture);
    }


    public SoilMoisture getLatestSoilMoistData(){
        return soilmoistRepository.getLatestRainData();
    }
}
