package uk.co.stephencathcart.smartspeedcamera;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Iterator;
import java.util.List;
import javax.jms.Message;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import uk.co.stephencathcart.common.MessageType;
import uk.co.stephencathcart.common.SmartSpeedCamera;
import uk.co.stephencathcart.common.Snapshot;

/**
 * Responsible for sending messages to the Service Bus using JMS.
 *
 * @author Stephen Cathcart
 */
@Service
public class TopicPublisher {

    private static final Logger log = LoggerFactory.getLogger(TopicPublisher.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * As the application can run offline, we may have a list of Snapshots to
     * send. We serialize the Snapshot to a json string and set the data
     * property to this. We also specify the message type to be snapshot to
     * differentiate between these and camera registration messages. Finally we
     * set a boolean property to specify if the car was speeding or not.
     */
    public boolean sendSnapshot(List<Snapshot> snapshots) {
        boolean isSuccess = true;

        for (Iterator<Snapshot> it = snapshots.iterator(); it.hasNext();) {
            try {
                Snapshot snapshot = it.next();
                final ObjectMapper mapper = new ObjectMapper();
                final String json = mapper.writeValueAsString(snapshot);
                jmsTemplate.send("speed-camera-topic", (Session session) -> {
                    Message message = session.createMessage();
                    message.setStringProperty("messageType", MessageType.SNAPSHOT.name());
                    message.setBooleanProperty("speeding", snapshot.isSpeeding());
                    message.setStringProperty("data", json);
                    
                    return message;
                });
                it.remove();
            } catch (Exception ex) {
                isSuccess = false;
                break;
            }
        }
        return isSuccess;
    }

    /**
     * Sends a registration message to the Service bus when a camera is switched
     * on. We serialize the camera to a json string and set the data property to
     * this. We also specify the message type to be snapshot to differentiate
     * between these and Snapshot messages.
     */
    public boolean sendCameraRegistration(SmartSpeedCamera camera) {
        boolean isSuccess = true;
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String json = mapper.writeValueAsString(camera);
            jmsTemplate.send("speed-camera-topic", (Session session) -> {
                Message message = session.createMessage();
                message.setStringProperty("messageType", MessageType.REGISTRATION.name());
                message.setStringProperty("data", json);
                return message;
            });
        } catch (Exception ex) {
            isSuccess = false;
        }
        return isSuccess;
    }
}
