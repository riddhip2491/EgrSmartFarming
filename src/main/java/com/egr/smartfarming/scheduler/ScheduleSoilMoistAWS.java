package com.egr.smartfarming.scheduler;

import com.amazonaws.services.iot.client.*;
import com.egr.smartfarming.service.NonBlockingPublishListener;
import com.egr.smartfarming.service.SoilMoistAWSService;
import com.egr.smartfarming.service.SoilMoistTopicListener;
import com.egr.smartfarming.utils.SampleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleSoilMoistAWS {

    private static final String TestTopic = "iotbutton/G030MD044373XVDU"; //iobutton/G030MD043422K4J8

    private static final AWSIotQos TestTopicQos = AWSIotQos.QOS0;
    private static AWSIotMqttClient awsIotClient;
    @Value("${aws.clientendpoint}")
    private String clientEndpoint;

    @Value("${aws.sunlightclientid}")
    private String sunlightClientId;

    @Value("${aws.sunlightcertificatefile}")
    private String sunlightCertificateFile;
    @Value("${aws.sunlightprivatekeyfile}")
    private String sunlightPrivateKeyFile;

    @Value("${aws.keyalgorithm}")
    private String keyAlgorithm;

    @Value("${aws.awsaccesskeyid}")
    private String awsAccessKeyIdProp;

    @Value("${aws.awssecretaccesskey}")
    private String awsSecretAccessKeyProp;

    @Value("${aws.sessiontoken}")
    private String sessionTokenProp;

    SoilMoistAWSService soilMoistAWSService;

    @Autowired
    public ScheduleSoilMoistAWS(SoilMoistAWSService soilMoistAWSService){
        this.soilMoistAWSService = soilMoistAWSService;
    }
    @Scheduled(cron="0 * * * * ?")
    public void getSinlightButtonData() throws InterruptedException, AWSIotException, AWSIotTimeoutException {
        if(awsIotClient == null || awsIotClient.getConnectionStatus() == null) {
            if (awsIotClient == null && sunlightCertificateFile != null && sunlightPrivateKeyFile != null) {
                String algorithm = keyAlgorithm;
                if (algorithm.equalsIgnoreCase(""))
                    algorithm = null;
                SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(sunlightCertificateFile, sunlightPrivateKeyFile, algorithm);

                awsIotClient = new AWSIotMqttClient(clientEndpoint, sunlightClientId, pair.keyStore, pair.keyPassword);
            }
            if (awsIotClient == null) {
                String awsAccessKeyId = awsAccessKeyIdProp;
                String awsSecretAccessKey = awsSecretAccessKeyProp;
                String sessionToken = sessionTokenProp;

                if (awsAccessKeyId != null && awsSecretAccessKey != null) {
                    awsIotClient = new AWSIotMqttClient(clientEndpoint, sunlightClientId, awsAccessKeyId, awsSecretAccessKey,
                            sessionToken);
                }
            }

            if (awsIotClient == null) {
                throw new IllegalArgumentException("Failed to construct client due to missing certificate or credentials.");
            }
        }
        awsIotClient.connect();

        AWSIotTopic topic = new SoilMoistTopicListener(TestTopic, TestTopicQos,soilMoistAWSService);
        awsIotClient.subscribe(topic, true);


        Thread nonBlockingPublishThread = new Thread(new NonBlockingPublisher(awsIotClient));
    //    nonBlockingPublishThread.start();

    }

    public static class NonBlockingPublisher implements Runnable {
        private final AWSIotMqttClient awsIotClient;

        public NonBlockingPublisher(AWSIotMqttClient awsIotClient) {
            this.awsIotClient = awsIotClient;
        }

        @Override
        public void run() {
            long counter = 1;

            while (true) {
                String payload = "hello from non-blocking publisher - " + (counter++);
                AWSIotMessage message = new NonBlockingPublishListener(TestTopic, TestTopicQos, payload);
                if(message != null){
                    String result = message.getStringPayload();
                }
                try {
                    awsIotClient.publish(message);
                } catch (AWSIotException e) {
                    System.out.println(System.currentTimeMillis() + ": publish failed for " + payload);
                }
            }
        }
    }


    public static void setClient(AWSIotMqttClient client) {
        awsIotClient = client;
    }

}
