package com.egr.smartfarming.repository;

import com.egr.smartfarming.model.Decision;
import com.egr.smartfarming.model.SeasonIndia;
import com.egr.smartfarming.model.Temperature;
import org.json.simple.JSONObject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Repository
public interface SeasonRepository extends CrudRepository<SeasonIndia, Integer>{
     SeasonIndia save(SeasonIndia seasonIndia);

     @Query(value="select * from seasonindia",nativeQuery = true)
     ArrayList<SeasonIndia> getAllSeasons();

     @Query(value="select * from seasonindia where startmonth=?", nativeQuery = true)
     SeasonIndia findBySeason(String season);
     @Query(value= "select c.crop_name\n" +
             "from crop c, crop_soil soil, soiltype stype, crop_type ctype, crop_temperature ctemp,\n" +
             "\tcrop_rainfall crain \n" +
             " where stype.soil_id = soil.soil_type \n" +
             " and c.crop_id = ctype.crop_id\n" +
             " and c.crop_id = ctemp.crop_id\n" +
             " and c.crop_id = crain.crop_id\n" +
             " and c.crop_id = soil.crop_id\n" +
             " and \n" +
             " STYPE.soil_name = ? AND\n" +
             " CTYPE.CROP_TYPE = ?", nativeQuery = true)
     ArrayList<String> getCropDecision(String soilName, String cropType);


     @Query(value = "select distinct ctemp.avg_temp, (ctemp.min_temp -5), (ctemp.max_temp + 5) \n" +
             "from crop c, crop_soil soil, soiltype stype, crop_type ctype, crop_temperature ctemp,\n" +
             "\tcrop_rainfall crain \n" +
             " where stype.soil_id = soil.soil_type \n" +
             " and c.crop_id = ctype.crop_id\n" +
             " and c.crop_id = ctemp.crop_id\n" +
             " and c.crop_id = crain.crop_id\n" +
             " and c.crop_id = soil.crop_id\n" +
             " and \n" +
             " C.CROP_NAME = ?", nativeQuery = true)
     ArrayList<Object> getCropTemperature(String crop);
}
