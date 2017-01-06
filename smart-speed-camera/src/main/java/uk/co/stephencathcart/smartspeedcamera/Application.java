package uk.co.stephencathcart.smartspeedcamera;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import uk.co.stephencathcart.common.SmartSpeedCamera;

/**
 * Main Spring Boot class which registers a smart speed camera on start and then
 * monitors vehicles, taking a Snapshot and sending this to the Service Bus.
 *
 * @author Stephen Cathcart
 */
@SpringBootApplication
@EnableScheduling
@EnableJms
public class Application {

    @Autowired
    private SmartSpeedCamera camera;

    @Autowired
    private RecognitionSoftware recognitionSoftware;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Runs once on start up and attempts to register the camera by sending a
     * message to the Service Bus.
     */
    @PostConstruct
    public void init() {
        recognitionSoftware.register(camera);
    }

    /**
     * A scheduled monitoring job which periodically creates a Snapshot of a
     * passing vehicle and sends the message to the Service Bus. The cron timer
     * is specified in the application.properies file. If the camera failed to
     * register on start up we keep trying before capturing Snapshots.
     */
    @Scheduled(cron = "${app.numberofvehiclespermin}")
    public void monitor() {
        if (recognitionSoftware.getHasRegistered()) {
            recognitionSoftware.capture(camera);
        } else {
            recognitionSoftware.register(camera);
        }
    }
}
