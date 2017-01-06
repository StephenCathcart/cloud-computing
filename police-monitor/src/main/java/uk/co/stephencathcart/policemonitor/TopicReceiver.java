package uk.co.stephencathcart.policemonitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.jms.JMSException;
import org.apache.qpid.jms.message.JmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import uk.co.stephencathcart.common.DateUtils;
import uk.co.stephencathcart.common.Snapshot;

/**
 * Responsible for receiving messages from the Service Bus using JMS.
 *
 * @author Stephen Cathcart
 */
@Service
public class TopicReceiver {

    private static final Logger log = LoggerFactory.getLogger(AzureRepository.class);

    @Autowired
    AzureRepository repo;

    @Autowired
    private QueuePublisher topicPublisher;

    @Value("${app.rushhour.rate}")
    private Long rushHourRate;

    /**
     * Receives snapshots from the Service Bus of all speeding vehicles. We then
     * check if the vehicle is a priority and then log the result to the
     * terminal before persisting the data to Azure Table storage. Finally, we
     * send the snapshot off for a stolen vehicle check. Message rate is reduced
     * when the time is not rush hour to prevent hitting Azure Services
     * unnecessarily.
     */
    @JmsListener(destination = "speed-camera-topic",
            containerFactory = "topicJmsListenerContainerFactory",
            subscription = "police-subscription")
    public void onMessage(JmsMessage message) throws JMSException, IOException, URISyntaxException, InterruptedException {
        final ObjectMapper mapper = new ObjectMapper();

        if (message.propertyExists("data")) {
            Snapshot snapshot = mapper.readValue(message.getStringProperty("data"), Snapshot.class);
            boolean isPriority = isVehiclePriority(snapshot);
            terminal(snapshot, isPriority);

            repo.saveSpeedingSnapshot(isPriority, snapshot);
            topicPublisher.sendForVehicleChecking(snapshot);

            if (!DateUtils.isRushHour()) {
                Thread.sleep(rushHourRate);
            }
        }
    }

    /**
     * Logs the speeding snapshot to the terminal, printing PRIORITY if the
     * snapshot is a priority.
     */
    private void terminal(Snapshot snapshot, boolean isPriority) {
        if (isPriority) {
            log.info("PRIORITY: " + snapshot);
        } else {
            log.info(snapshot.toString());
        }
    }

    /**
     * Checks if the vehicle is a priority by checking if the speeding vehicle
     * is traveling 10% over the speed limit.
     */
    private boolean isVehiclePriority(Snapshot snapshot) {
        int upperLimit = snapshot.getCamera().getMaxSpeedLimit() + (int) Math.floor(snapshot.getCamera().getMaxSpeedLimit() * 0.1);
        return snapshot.getCurrentSpeed() > upperLimit;
    }
}
