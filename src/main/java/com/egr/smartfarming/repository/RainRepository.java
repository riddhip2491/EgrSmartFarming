package com.egr.smartfarming.repository;

import com.egr.smartfarming.model.Rainfall;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RainRepository extends CrudRepository<Rainfall, Long>{
    public Rainfall save(Rainfall rainfall);

    @Query(value= "select distinct * from rainfall order by currenttimeinmillis desc limit 1", nativeQuery = true)
    public Rainfall getLatestRainData();
}
