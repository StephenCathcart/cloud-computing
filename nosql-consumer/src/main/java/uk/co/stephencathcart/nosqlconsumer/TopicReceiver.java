package uk.co.stephencathcart.nosqlconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.jms.JMSException;
import org.apache.qpid.jms.message.JmsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import uk.co.stephencathcart.common.MessageType;
import uk.co.stephencathcart.common.SmartSpeedCamera;
import uk.co.stephencathcart.common.Snapshot;

/**
 * Responsible for receiving messages from the Service Bus using JMS.
 *
 * @author Stephen Cathcart
 */
@Service
public class TopicReceiver {

    @Autowired
    AzureRepository repo;
    
    @Value("${app.rushhour.rate}")
    private Long rushHourRate;

    /**
     * Based on the message type of either a snapshot or camera registration we
     * get that data then send it to the repository to persist it. Message rate
     * is reduced when the time is not rush hour to prevent hitting Azure
     * Services unnecessarily.
     */
    @JmsListener(destination = "speed-camera-topic",
            containerFactory = "topicJmsListenerContainerFactory",
            subscription = "default-subscription")
    public void onMessage(JmsMessage message) throws JMSException, IOException, URISyntaxException, InterruptedException {
        final ObjectMapper mapper = new ObjectMapper();

        if (message.propertyExists("messageType")) {
            switch (MessageType.valueOf(message.getStringProperty("messageType"))) {
                case SNAPSHOT:
                    repo.saveSnapshot(mapper.readValue(message.getStringProperty("data"), Snapshot.class));
                    if (!DateUtils.isRushHour()) {
                        Thread.sleep(rushHourRate);
                    }
                    break;
                case REGISTRATION:
                    repo.saveCameraRegistration(mapper.readValue(message.getStringProperty("data"), SmartSpeedCamera.class));
                    break;
            }
        }
    }
}
