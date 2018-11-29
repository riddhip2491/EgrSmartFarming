package com.egr.smartfarming.repository;

import com.egr.smartfarming.model.Decision;
import com.egr.smartfarming.model.SeasonIndia;
import com.egr.smartfarming.model.Temperature;
import org.json.simple.JSONObject;
import org.omg.CORBA.OBJ_ADAPTER;
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

     @Query(value="select cropseason from seasonindia", nativeQuery = true)
     ArrayList<String> findAllSeasons();

     @Query(value="select * from seasonindia where startmonth=?", nativeQuery = true)
     SeasonIndia findBySeason(String season);
     @Query(value= "select c.crop_name\n" +
             "from crop c, crop_soil soil, soiltype stype, crop_type ctype, crop_temperature ctemp,\n" +
             "\tcrop_rainfall crain, crop_location loc \n" +
             " where stype.soil_id = soil.soil_type \n" +
             " and c.crop_id = ctype.crop_id\n" +
             " and c.crop_id = ctemp.crop_id\n" +
             " and c.crop_id = crain.crop_id\n" +
             " and c.crop_id = soil.crop_id\n" +
             " and c.crop_id = loc.crop_id and \n" +
             " STYPE.soil_name = ? AND\n" +
             " CTYPE.CROP_TYPE = ? ", nativeQuery = true)
     ArrayList<String> getCropDecision(String soilName, String cropType);

     @Query(value = "select c.crop_name, stype.soil_name, ctype.crop_type, ctemp.AVG_TEMP, ctemp.min_temp, ctemp.max_temp,\n" +
             "\t\tcrain.AVG_RAIN, crain.MIN_RAIN, crain.MAX_RAIN\n" +
             "from crop c, crop_soil soil, soiltype stype, crop_type ctype, crop_temperature ctemp,\n" +
             "\tcrop_rainfall crain, crop_location loc\n" +
             " where stype.soil_id = soil.soil_type \n" +
             " and c.crop_id = ctype.crop_id\n" +
             " and c.crop_id = soil.crop_id\n" +
             " and \n" +
             " STYPE.soil_name = ? AND\n" +
             " CTYPE.CROP_TYPE = ?", nativeQuery = true)
     ArrayList<Object> getCropDecisionNew(String soilName,String cropType);



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


     @Query(value = "select distinct\n" +
             "\t\tcrain.AVG_RAIN, crain.MIN_RAIN, crain.MAX_RAIN\n" +
             "from crop c, crop_soil soil, soiltype stype, crop_type ctype, crop_temperature ctemp,\n" +
             "\tcrop_rainfall crain, crop_location loc\n" +
             " where stype.soil_id = soil.soil_type \n" +
             " and c.crop_id = ctype.crop_id\n" +
             " and c.crop_id = ctemp.crop_id\n" +
             " and c.crop_id = crain.crop_id\n" +
             " and c.crop_id = soil.crop_id\n" +
             " and c.crop_id = loc.crop_id\n" +
             " and \n" +
             " c.crop_name= ?", nativeQuery = true)
     ArrayList<Object> getCropRainfall(String crop);

     @Query(value = "SELECT C.CROP_NAME FROM crop_rainfall RAIN, CROP C WHERE C.CROP_ID = RAIN.CROP_ID AND C.CROP_ID IN (\n" +
             " SELECT c.CROP_id FROM crop_temperature temp, crop c WHERE  temp.crop_id = c.crop_id and c.CROP_ID IN (\n" +
             " SELECT C.CROP_ID FROM CROP C, CROP_SOIL SOIL WHERE C.CROP_ID = SOIL.CROP_ID AND\n" +
             "  SOIL.SOIL_TYPE = ? AND C.CROP_ID IN (SELECT C.CROP_ID FROM CROP_TYPE CTYPE, CROP C, CROP_SOIL \n" +
             "WHERE C.CROP_ID = CTYPE.CROP_ID AND CROP_TYPE=?)) AND\n" +
             "MIN_TEMP >= ? AND MAX_TEMP<= ?) AND RAIN.MIN_RAIN >= ? AND rAIN.MAX_RAIN <= ?", nativeQuery = true)
     ArrayList<String> processCropDecision(int soilType, String cropType,double minTemp, double maxTemp,double minRain, double maxRain );

     @Query(value ="select soil_id from soiltype where soil_name=?", nativeQuery = true)
     int getSoilType(String soilName);

     @Query(value = "select distinct(c.crop_name) from crop c, crop_location l where c.crop_id = l.crop_id and state = ?", nativeQuery = true)
     ArrayList<String> getCropByLocation(String name);

     @Query(value = "select startmonth, endmonth from seasonindia where cropseason= ?", nativeQuery = true)
     Object getMonths(String seasonType);

     @Query(value = "SELECT C.CROP_NAME FROM CROP_PH PH, CROP C WHERE PH.CROP_ID = C.CROP_ID AND C.CROP_ID IN (\n" +
             " SELECT C.CROP_ID FROM crop_rainfall RAIN, CROP C WHERE C.CROP_ID = RAIN.CROP_ID AND C.CROP_ID IN (\n" +
             " SELECT c.CROP_id FROM crop_temperature temp, crop c WHERE  temp.crop_id = c.crop_id and c.CROP_ID IN (\n" +
             " SELECT C.CROP_ID FROM CROP C, CROP_SOIL SOIL WHERE C.CROP_ID = SOIL.CROP_ID AND\n" +
             "  SOIL.SOIL_TYPE = ? AND C.CROP_ID IN (SELECT C.CROP_ID FROM CROP_TYPE CTYPE, CROP C, CROP_SOIL \n" +
             "WHERE C.CROP_ID = CTYPE.CROP_ID AND CROP_TYPE=?)) AND\n" +
             "MIN_TEMP >= ? AND MAX_TEMP<= ?) AND RAIN.MIN_RAIN >= ? AND RAIN.MAX_RAIN <= ?) \n" +
             "and crop_min_ph >= ? and crop_max_ph <= ?", nativeQuery = true)
     ArrayList<String> getCropDecisionByPH(int soilType, String cropType,double minTemp, double maxTemp,double minRain, double maxRain, double pH, double maxpH);

     @Query(value="SELECT C.CROP_NAME FROM CROP_EC EC, CROP C WHERE EC.CROP_ID = C.CROP_ID AND C.CROP_ID IN (\n" +
             "SELECT C.CROP_ID FROM crop_rainfall RAIN, CROP C WHERE C.CROP_ID = RAIN.CROP_ID AND C.CROP_ID IN (\n" +
             " SELECT c.CROP_id FROM crop_temperature temp, crop c WHERE  temp.crop_id = c.crop_id and c.CROP_ID IN (\n" +
             " SELECT C.CROP_ID FROM CROP C, CROP_SOIL SOIL WHERE C.CROP_ID = SOIL.CROP_ID AND\n" +
             "  SOIL.SOIL_TYPE = ? AND C.CROP_ID IN (SELECT C.CROP_ID FROM CROP_TYPE CTYPE, CROP C, CROP_SOIL \n" +
             "WHERE C.CROP_ID = CTYPE.CROP_ID AND CROP_TYPE=?)) AND\n" +
             "MIN_TEMP >= ? AND MAX_TEMP<= ?) AND RAIN.MIN_RAIN >= ? AND rAIN.MAX_RAIN <= ?)\n" +
             "AND EC.CROP_MIN_EC >= ?  AND EC.CROP_MAXI_ED <= ?", nativeQuery = true)
     ArrayList<String> getCropDecisionByEC(int soilType, String cropType,double minTemp, double maxTemp,double minRain, double maxRain, double eC, double maxEC);

     @Query(value= "SELECT C.CROP_NAME FROM CROP_EC EC, CROP C WHERE EC.CROP_ID = C.CROP_ID AND C.CROP_ID IN (\n" +
             "SELECT C.CROP_ID FROM CROP_PH PH, CROP C WHERE PH.CROP_ID = C.CROP_ID AND C.CROP_ID IN (\n" +
             " SELECT C.CROP_ID FROM crop_rainfall RAIN, CROP C WHERE C.CROP_ID = RAIN.CROP_ID AND C.CROP_ID IN (\n" +
             " SELECT c.CROP_id FROM crop_temperature temp, crop c WHERE  temp.crop_id = c.crop_id and c.CROP_ID IN (\n" +
             " SELECT C.CROP_ID FROM CROP C, CROP_SOIL SOIL WHERE C.CROP_ID = SOIL.CROP_ID AND\n" +
             "  SOIL.SOIL_TYPE = ? AND C.CROP_ID IN (SELECT C.CROP_ID FROM CROP_TYPE CTYPE, CROP C, CROP_SOIL \n" +
             "WHERE C.CROP_ID = CTYPE.CROP_ID AND CROP_TYPE=?)) AND\n" +
             "MIN_TEMP >= ? AND MAX_TEMP<= ?) AND RAIN.MIN_RAIN >= ? AND rAIN.MAX_RAIN <= ?) \n" +
             "and crop_min_ph >= ? and crop_max_ph <= ?)\n" +
             "AND EC.CROP_MIN_EC >= ? AND EC.CROP_MAXI_ED <= ?", nativeQuery = true)
     ArrayList<String> getCropDecisionByAll(int soilType, String cropType,double minTemp, double maxTemp,double minRain, double maxRain, double pH, double maxpH, double eC, double maxEC);

     @Query(value ="select ph.crop_min_ph, ph.crop_max_ph from crop c, crop_ph ph where c.crop_id = ph.crop_id and crop_name = ?", nativeQuery = true)
     ArrayList<Object> getCropBypH(String crop);

    @Query(value ="select ec.crop_min_ec, ec.crop_max_ec from crop c, crop_ec ec where c.crop_id = ec.crop_id and crop_name = ?", nativeQuery = true)
     ArrayList<Object> getCropByEC(String crop);
}
