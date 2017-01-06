package uk.co.stephencathcart.vehiclecheck;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
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
 * Responsible for receiving messages from the Service Bus Queue using JMS.
 *
 * @author Stephen Cathcart
 */
@Service
public class QueueReceiver {

    private static final Logger log = LoggerFactory.getLogger(QueueReceiver.class);

    @Autowired
    private VehicleCheckRepository repository;
    
    @Value("${app.rushhour.rate}")
    private Long rushHourRate;

    /**
     * Receives speeding snapshots from the police monitor application and
     * checks if the vehicle is stolen based on the registration. Message rate
     * is reduced when the time is not rush hour to prevent hitting Azure
     * Services unnecessarily.
     */
    @JmsListener(destination = "vehicle-check-queue")
    public void onMessage(JmsMessage message) throws JMSException, IOException, URISyntaxException, InterruptedException {
        final ObjectMapper mapper = new ObjectMapper();

        if (message.propertyExists("data")) {
            Snapshot snapshot = mapper.readValue(message.getStringProperty("data"), Snapshot.class);
            boolean isVehicleStolen = isVehicleStolen(snapshot.getRegistration());
            
            audit(snapshot.getRegistration(), isVehicleStolen);
            
            if (!DateUtils.isRushHour()) {
                Thread.sleep(rushHourRate);
            }
        }
    }

    /**
     * Simulates a long check by a third party to check if the vehicle is
     * stolen.
     */
    public boolean isVehicleStolen(String vehicleRegistration) {
        log.info("Vehicle Check: {}", vehicleRegistration);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            log.error("Error checking vehicle");
        }
        return (Math.random() < 0.70);
    }

    private void audit(String registration, boolean vehicleStolen) {
        VehicleCheck vehicle = new VehicleCheck();
        vehicle.setRegistration(registration);
        vehicle.setStolen(vehicleStolen);
        vehicle.setCheckedDate(new Date());
        repository.save(vehicle);
    }
}
