package com.egr.smartfarming.repository;

import com.egr.smartfarming.model.Temperature;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedNativeQuery;

@Repository
public interface TemperatureRepository {
  /*  @NamedNativeQuery(name="temperature",query = "select distinct ctemp.avg_temp, (ctemp.min_temp -5), (ctemp.max_temp + 5) \n" +
            "from crop c, crop_soil soil, soiltype stype, crop_type ctype, crop_temperature ctemp,\n" +
            "\tcrop_rainfall crain \n" +
            " where stype.soil_id = soil.soil_type \n" +
            " and c.crop_id = ctype.crop_id\n" +
            " and c.crop_id = ctemp.crop_id\n" +
            " and c.crop_id = crain.crop_id\n" +
            " and c.crop_id = soil.crop_id\n" +
            " and \n" +
            " C.CROP_NAME = ?",resultClass = Temperature.class)
    Temperature getCropTemperature(String crop);*/
}
