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
import com.egr.smartfarming.model.SoilMoisture;
import org.json.simple.JSONObject;

/**
 * This class extends {@link AWSIotTopic} to receive messages from a subscribed
 * topic.
 */
public class SoilMoistTopicListener extends AWSIotTopic {

    private SoilMoisture soilMoisture;
private SoilMoistAWSService soilMoistAWSService;
    public SoilMoistTopicListener(String topic, AWSIotQos qos, SoilMoistAWSService soilMoistAWSService) {
        super(topic, qos);
        this.soilMoistAWSService = soilMoistAWSService;
    }


    @Override
    public void onMessage(AWSIotMessage message) {
        try {

            System.out.println(System.currentTimeMillis() + ": <<< " + message.getStringPayload());
            String result = message.getStringPayload();
            if(result!= null){
                soilMoisture = new SoilMoisture();
                Object object = new org.json.simple.parser.JSONParser().parse(result);
                JSONObject jsonObject = (JSONObject) object;
                String srNo = (String) jsonObject.get("serialNumber");
                String batteryVoltage = (String) jsonObject.get("batteryVoltage");
                String clickType = (String) jsonObject.get("clickType");
                soilMoisture.setCurrentTimeinMillis(System.currentTimeMillis());
                soilMoisture.setSerialNumber(srNo);
                soilMoisture.setBatteryVoltage(batteryVoltage);
                soilMoisture.setClickType(clickType);
                soilMoistAWSService.saveSoilMoistData(soilMoisture);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
