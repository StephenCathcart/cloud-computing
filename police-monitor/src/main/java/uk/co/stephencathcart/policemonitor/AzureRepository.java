package uk.co.stephencathcart.policemonitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TablePayloadFormat;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.co.stephencathcart.common.Snapshot;
import uk.co.stephencathcart.common.SpeedingVehicleEntity;

/**
 * Repository responsible for persisting messages from the Service Bus to an
 * Azure Storage Table.
 *
 * @author Stephen Cathcart
 */
@Repository
public class AzureRepository {

    private static final Logger log = LoggerFactory.getLogger(AzureRepository.class);

    @Autowired
    private CloudStorageAccount account;

    /**
     * Saves the speeding snapshot details to a Table called SpeedingVehicles.
     * We create the table if it does not exist already.
     */
    public void saveSpeedingSnapshot(boolean isPriority, Snapshot snapshot) throws URISyntaxException, JsonProcessingException {
        try {
            // Create the table client.
            CloudTableClient tableClient = account.createCloudTableClient();
            tableClient.getDefaultRequestOptions().setTablePayloadFormat(TablePayloadFormat.JsonNoMetadata);

            // Create the table if it doesn't exist.
            String tableName = "SpeedingVehicles";
            CloudTable cloudTable = tableClient.getTableReference(tableName);
            cloudTable.createIfNotExists();

            SpeedingVehicleEntity entity = new SpeedingVehicleEntity(isPriority, snapshot);

            TableOperation insertSpeedingVehicle = TableOperation.insertOrReplace(entity);
            cloudTable.execute(insertSpeedingVehicle);
        } catch (StorageException ex) {
            log.error("Error saving speeding details");
        }
    }
}
