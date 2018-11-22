package com.egr.smartfarming.repository;

import com.egr.smartfarming.model.SoilMoisture;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoilmoistRepository extends CrudRepository<SoilMoisture, Long>{
    public  SoilMoisture save(SoilMoisture soilMoisture);

    @Query(value= "select distinct * from soilmoisture order by currenttimeinmillis desc limit 1", nativeQuery = true)
    public SoilMoisture getLatestRainData();
}
