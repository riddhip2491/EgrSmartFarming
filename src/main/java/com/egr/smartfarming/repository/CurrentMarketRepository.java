package com.egr.smartfarming.repository;

import com.egr.smartfarming.model.CurrentMarket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sun.util.resources.cldr.ebu.CurrencyNames_ebu;

import javax.persistence.NamedNativeQuery;
import java.util.ArrayList;

@Repository
public interface CurrentMarketRepository extends CrudRepository<CurrentMarket, Long>{
    CurrentMarket save(CurrentMarket currentMarket);

    ArrayList<CurrentMarket> findAllByCommodity(String commodity);

    /*@Query(value = "select * from current_market", nativeQuery = true)
    ArrayList<CurrentMarket> getAllByIdExists();

    ArrayList<CurrentMarket> deleteAllByIdExists();*/
}
