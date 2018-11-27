package com.egr.smartfarming.scheduler;

import com.egr.smartfarming.model.CurrentMarket;
import com.egr.smartfarming.repository.CurrentMarketRepository;
import com.egr.smartfarming.repository.SeasonRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;

public class CurrentMarketScheduler {


    @Autowired
    private CurrentMarketRepository currentMarketRepository;

    @Value("${gov.commodities.api}")
    private String currentMarketApi;

    @Value("${gov.commodities.key}")
    private String currentMarketKey;

    private CurrentMarket currentMarket;

    public CurrentMarketScheduler(CurrentMarketRepository currentMarketRepository){
        this.currentMarketRepository = currentMarketRepository;
    }

    @Scheduled(cron="0 0 6 1/1 * ? *")
    private void getCurrentMarket(){
        try {
            String uri = currentMarketApi + currentMarketKey + "&format=json";
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);
            System.out.println("result: \n" + result);
            Object object = new JSONParser().parse(result);
            JSONObject jsonObject = (JSONObject) object;
            JSONArray records = (JSONArray) jsonObject.get("records");
            Iterator iterator= records.iterator();
            while (iterator.hasNext()){
                currentMarket = new CurrentMarket();
                JSONObject jsonObject1 = (JSONObject)iterator.next();
                String market = (String) jsonObject1.get("market");
                String commodity = (String) jsonObject1.get("commodity");
                long maxPrice = (long)jsonObject1.get("max_price");
                long minPrice = (long)jsonObject1.get("min_price");
                String variety = (String)jsonObject1.get("variety");
                long modalPrice = (long)jsonObject1.get("modal_price");
                String district = (String)jsonObject1.get("district");
                String state = (String)jsonObject1.get("state");
                String arrivalDate = (String)jsonObject1.get("arrival_date");
                long timestamp = (long)jsonObject1.get("timestamp");
                //currentMarket.setId(System.currentTimeMillis());
                currentMarket.setMarket(market);
                currentMarket.setArrivalDate(arrivalDate);
                currentMarket.setCommodity(commodity);
                currentMarket.setDistrict(district);
                currentMarket.setMaxPrice(maxPrice);
                currentMarket.setMinPrice(minPrice);
                currentMarket.setModalPrice(modalPrice);
                currentMarket.setVariety(variety);
                currentMarket.setState(state);
                currentMarket.setTimestamp(timestamp);
                currentMarketRepository.save(currentMarket);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
