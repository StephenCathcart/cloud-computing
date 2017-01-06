/**
 * Heavy referenced from the tutorial "Using Azure Service Bus with Spring JMS"
 * by Ed Hillmann @ http://ramblingstechnical.blogspot.co.uk/p/using-azure-service-bus-with-spring-jms.html
 */
package uk.co.stephencathcart.smartspeedcamera;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import javax.jms.ConnectionFactory;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * Spring configuration class for configuring the JMS connection factory and JMS
 * template. The client id is stored in the application.properties file. We
 * enable auto reconnect on exception in case we have network issues.
 *
 * @author Stephen Cathcart
 */
@Configuration
public class MessagingConfig {

    @Value("${jms.clientid}")
    private String clientId;

    @Bean
    public ConnectionFactory jmsConnectionFactory(MessageStoreDetails details) throws UnsupportedEncodingException {
        JmsConnectionFactory jmsConnectionfactory = new JmsConnectionFactory(details.getUrlString());
        jmsConnectionfactory.setUsername(details.getUsername());
        jmsConnectionfactory.setPassword(details.getPassword());
        jmsConnectionfactory.setReceiveLocalOnly(true);
        jmsConnectionfactory.setClientID(clientId + UUID.randomUUID());

        SingleConnectionFactory singleConnectionFactory = new SingleConnectionFactory(jmsConnectionfactory);
        singleConnectionFactory.setReconnectOnException(true);
        return singleConnectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory jmsConnectionFactory) {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(jmsConnectionFactory);
        return template;
    }
}
