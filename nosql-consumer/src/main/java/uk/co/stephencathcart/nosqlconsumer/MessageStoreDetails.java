/**
 * Heavy referenced from the tutorial "Using Azure Service Bus with Spring JMS"
 * by Ed Hillmann @ http://ramblingstechnical.blogspot.co.uk/p/using-azure-service-bus-with-spring-jms.html
 */
package uk.co.stephencathcart.nosqlconsumer;

import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Spring component to be used by the JMS connection factory to connect to the
 * Service Bus. All values are stored in the application.properties file.
 *
 * @author Stephen Cathcart
 */
@Component
public class MessageStoreDetails {

    @Value("${namespace.host}")
    private String host;
    
    @Value("${namespace.username}")
    private String username;
    
    @Value("${namespace.password}")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrlString() throws UnsupportedEncodingException {
        return String.format("amqps://%1s?amqp.idleTimeout=3600000", host);
    }
}
