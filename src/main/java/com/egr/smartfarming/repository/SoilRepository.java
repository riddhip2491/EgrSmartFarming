package com.egr.smartfarming.repository;


import com.egr.smartfarming.model.SoilType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface SoilRepository extends CrudRepository<SoilType, Integer>{

    @Query(value = "select soil_name from soiltype",nativeQuery = true)
    ArrayList<String> getSoilTypes();


}
