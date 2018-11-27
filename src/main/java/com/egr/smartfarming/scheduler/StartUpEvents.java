package com.egr.smartfarming.scheduler;

import com.egr.smartfarming.model.SeasonIndia;
import com.egr.smartfarming.service.StartUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class StartUpEvents implements ApplicationListener<ApplicationReadyEvent>{

    @Autowired
    private StartUpService startUpService;


    private SeasonIndia seasonIndia;

    public StartUpEvents(StartUpService startUpService){
        this.startUpService = startUpService;
    }
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        //Setting seasons of india
        List list = addSeason();
        Iterator iterator = list.iterator();
        while(iterator.hasNext()) {
           // SeasonIndia seasonIndia = (SeasonIndia) iterator.next();
            startUpService.saveSeason((SeasonIndia) iterator.next());
        }
    }

    private List<SeasonIndia> addSeason(){
        List list = new ArrayList<SeasonIndia>();
        //Summer season
        seasonIndia = new SeasonIndia();
        //seasonIndia.setSeasonId(0);
        seasonIndia.setSeason("Summer");
        seasonIndia.setCropSeasonType("Summer");
        seasonIndia.setStartMonth("March");
        seasonIndia.setEndMonth("June");
        list.add(seasonIndia);
        seasonIndia = new SeasonIndia();
        seasonIndia.setSeason("Monsoon");
        seasonIndia.setCropSeasonType("Kharif");
        seasonIndia.setStartMonth("July");
        seasonIndia.setEndMonth("October");
        list.add(seasonIndia);
        seasonIndia = new SeasonIndia();
        seasonIndia.setSeason("Winter");
        seasonIndia.setCropSeasonType("Rabi");
        seasonIndia.setStartMonth("October");
        seasonIndia.setEndMonth("March");
        list.add(seasonIndia);
        return  list;
    }
}
