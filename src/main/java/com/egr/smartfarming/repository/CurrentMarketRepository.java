package com.egr.smartfarming.repository;

import com.egr.smartfarming.model.CurrentMarket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sun.util.resources.cldr.ebu.CurrencyNames_ebu;

import java.util.ArrayList;

@Repository
public interface CurrentMarketRepository extends CrudRepository<CurrentMarket, Long>{
    CurrentMarket save(CurrentMarket currentMarket);

    ArrayList<CurrentMarket> findAllByCommodity(String commodity);
}
