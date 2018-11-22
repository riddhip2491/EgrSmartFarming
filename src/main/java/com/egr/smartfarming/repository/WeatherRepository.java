package com.egr.smartfarming.repository;

import com.egr.smartfarming.model.Weather;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends CrudRepository<Weather, Long> {
    Weather findByCurrenttimeinmillis(double currentTimeinMillis);
}
