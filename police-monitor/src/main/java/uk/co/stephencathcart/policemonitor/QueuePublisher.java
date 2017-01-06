package uk.co.stephencathcart.policemonitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.jms.Message;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import uk.co.stephencathcart.common.Snapshot;

/**
 * Responsible for sending messages to the Service Bus Queue using JMS for
 * vehicle checks.
 *
 * @author Stephen Cathcart
 */
@Service
public class QueuePublisher {

    private static final Logger log = LoggerFactory.getLogger(QueuePublisher.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Sends the speeding snapshot over to another Service Bus Queue to be
     * processed by a vehicle check application.
     */
    public boolean sendForVehicleChecking(Snapshot snapshot) {
        boolean isSuccess = true;
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String json = mapper.writeValueAsString(snapshot);
            jmsTemplate.send("vehicle-check-queue", (Session session) -> {
                Message message = session.createMessage();
                message.setStringProperty("data", json);
                return message;
            });
        } catch (Exception ex) {
            isSuccess = false;
        }
        return isSuccess;
    }
}
