package uk.co.stephencathcart.nosqlconsumer;

import com.microsoft.azure.storage.CloudStorageAccount;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for configuring a Cloud Storage Account to be used
 * globally. The connection string is stored in the application.properties file.
 *
 * @author Stephen Cathcart
 */
@Configuration
public class AzureConfig {

    @Value("${azure.connection}")
    private String connection;

    @Bean
    public CloudStorageAccount cloudStorageAccount() throws URISyntaxException, InvalidKeyException {
        return CloudStorageAccount.parse(connection);
    }
}
