package com.egr.smartfarming.service;

import com.egr.smartfarming.model.SeasonIndia;
import com.egr.smartfarming.model.SoilType;
import com.egr.smartfarming.repository.SeasonRepository;
import com.egr.smartfarming.repository.SoilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("startUpService")
public class StartUpService {

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private SoilRepository soilRepository;

    public StartUpService(SeasonRepository seasonRepository, SoilRepository soilRepository){
        this.seasonRepository = seasonRepository;
        this.soilRepository = soilRepository;
    }

    public void saveSeason(SeasonIndia seasonIndia){
        seasonRepository.save(seasonIndia);
    }


    public ArrayList<String> getSoilTypes(){
        return soilRepository.getSoilTypes();
    }
}
