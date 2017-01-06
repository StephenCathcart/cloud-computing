package uk.co.stephencathcart.nosqlconsumer;

import uk.co.stephencathcart.common.SnapshotEntity;
import uk.co.stephencathcart.common.SmartSpeedCameraEntity;
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
import uk.co.stephencathcart.common.SmartSpeedCamera;
import uk.co.stephencathcart.common.Snapshot;

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
     * Saves camera registration details to a Table called CameraRegistrations.
     * We create the table if it does not exist already.
     */
    public void saveCameraRegistration(SmartSpeedCamera camera) throws URISyntaxException, JsonProcessingException {
        try {
            log.info("Persisting: {}", camera);
            
            // Create the table client.
            CloudTableClient tableClient = account.createCloudTableClient();
            tableClient.getDefaultRequestOptions().setTablePayloadFormat(TablePayloadFormat.JsonNoMetadata);

            // Create the table if it doesn't exist.
            String tableName = "CameraRegistrations";
            CloudTable cloudTable = tableClient.getTableReference(tableName);
            cloudTable.createIfNotExists();

            SmartSpeedCameraEntity entity = new SmartSpeedCameraEntity(camera);

            TableOperation insertSnapshot = TableOperation.insertOrReplace(entity);
            cloudTable.execute(insertSnapshot);
        } catch (StorageException ex) {
            log.error("Error saving camera registrations");
        }
    }

    /**
     * Saves snapshot details to a Table called Sightings. We create the table
     * if it does not exist already.
     */
    public void saveSnapshot(Snapshot snapshot) throws URISyntaxException, JsonProcessingException {
        try {
            log.info("Persisting: {}", snapshot);
            
            // Create the table client.
            CloudTableClient tableClient = account.createCloudTableClient();
            tableClient.getDefaultRequestOptions().setTablePayloadFormat(TablePayloadFormat.JsonNoMetadata);

            // Create the table if it doesn't exist.
            String tableName = "Sightings";
            CloudTable cloudTable = tableClient.getTableReference(tableName);
            cloudTable.createIfNotExists();

            SnapshotEntity entity = new SnapshotEntity(snapshot);

            TableOperation insertSnapshot = TableOperation.insertOrReplace(entity);
            cloudTable.execute(insertSnapshot);
        } catch (StorageException ex) {
            log.error("Error saving snapshots");
        }
    }
}
