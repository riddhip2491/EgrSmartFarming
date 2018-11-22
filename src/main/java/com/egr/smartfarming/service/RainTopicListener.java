/*
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.egr.smartfarming.service;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.egr.smartfarming.model.Rainfall;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This class extends {@link AWSIotTopic} to receive messages from a subscribed
 * topic.
 */
public class RainTopicListener extends AWSIotTopic {


    private Rainfall rainfall;
    RainfallService rainfallService;


    public RainTopicListener(String topic, AWSIotQos qos, RainfallService   rainfallService) {
        super(topic, qos);
        this.rainfallService  = rainfallService;
    }


    @Override
    public void onMessage(AWSIotMessage message) {
        try {

            System.out.println(System.currentTimeMillis() + ": <<< " + message.getStringPayload());
            String result = message.getStringPayload();
            if(result!= null){
                rainfall = new Rainfall();
                Object object = new org.json.simple.parser.JSONParser().parse(result);
                JSONObject jsonObject = (JSONObject) object;
                String srNo = (String) jsonObject.get("serialNumber");
                String batteryVoltage = (String) jsonObject.get("batteryVoltage");
                String clickType = (String) jsonObject.get("clickType");
                rainfall.setCurrentTimeinMillis(System.currentTimeMillis());
                rainfall.setSerialNumber(srNo);
                rainfall.setBatteryVoltage(batteryVoltage);
                rainfall.setClickType(clickType);
                rainfallService.saveRainData(rainfall);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
