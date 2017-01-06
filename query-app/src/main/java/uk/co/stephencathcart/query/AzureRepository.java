package uk.co.stephencathcart.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.co.stephencathcart.common.SmartSpeedCamera;
import uk.co.stephencathcart.common.SmartSpeedCameraEntity;
import uk.co.stephencathcart.common.Snapshot;
import uk.co.stephencathcart.common.SnapshotEntity;

/**
 * Repository responsible for getting data from the Azure Storage Table for
 * reporting purposes.
 *
 * @author Stephen Cathcart
 */
@Repository
public class AzureRepository {

    @Autowired
    private CloudStorageAccount account;

    /**
     * Returns a list of all smart camera registrations.
     */
    public List<SmartSpeedCamera> findAllCameraRegistrations() {
        List<SmartSpeedCamera> cameras = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            CloudTableClient tableClient = account.createCloudTableClient();
            CloudTable cloudTable = tableClient.getTableReference("CameraRegistrations");
            TableQuery<SmartSpeedCameraEntity> query = TableQuery.from(SmartSpeedCameraEntity.class);

            for (SmartSpeedCameraEntity entity : cloudTable.execute(query)) {
                cameras.add(mapper.readValue(entity.getSmartSpeedCamera(), SmartSpeedCamera.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cameras;
    }

    /**
     * Returns a list of all priority sightings / snapshots. To do this we
     * filter on the partition key which will contain wither 'NORMAL' or
     * 'PRIORITY'.
     */
    public List<Snapshot> findAllPrioritySightings() {
        List<Snapshot> sightings = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            CloudTableClient tableClient = account.createCloudTableClient();
            CloudTable cloudTable = tableClient.getTableReference("SpeedingVehicles");
            String partitionFilter = TableQuery.generateFilterCondition(
                    "PartitionKey",
                    QueryComparisons.EQUAL,
                    "PRIORITY");
            TableQuery<SnapshotEntity> query = TableQuery.from(SnapshotEntity.class)
                    .where(partitionFilter);

            for (SnapshotEntity entity : cloudTable.execute(query)) {
                sightings.add(mapper.readValue(entity.getSnapshot(), Snapshot.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sightings;
    }

    /**
     * Returns a historical list of all sightings for a particular vehicle based
     * on the registration. The Sightings table uses vehicle registrations for
     * its partition key so we can search using that.
     */
    public List<Snapshot> findSightingHistory(String registration) {
        List<Snapshot> sightings = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            CloudTableClient tableClient = account.createCloudTableClient();
            CloudTable cloudTable = tableClient.getTableReference("Sightings");
            String partitionFilter = TableQuery.generateFilterCondition(
                    "PartitionKey",
                    QueryComparisons.EQUAL,
                    registration);
            TableQuery<SnapshotEntity> query = TableQuery.from(SnapshotEntity.class)
                    .where(partitionFilter);

            for (SnapshotEntity entity : cloudTable.execute(query)) {
                sightings.add(mapper.readValue(entity.getSnapshot(), Snapshot.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sightings;
    }

    /**
     * <p>
     * NON-COURSEWORK FUNCTIONALITY: INCLUDED TO EASILY VIEW CAMERA DATA IN THE
     * FRONT-END CHART
     * </p>
     * Returns ChartData which includes a list of all unique registered cameras
     * and for each camera, the total count of snapshots made by the camera and
     * how many of those were speeding.
     */
    public ChartData findSnapshotData() {
        ChartData chartData = new ChartData();
        List<Snapshot> snapshots = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        Set<String> cameras = new TreeSet<>();
        List<Long> seriesData = new ArrayList<>();
        List<Long> speedingData = new ArrayList<>();

        try {
            CloudTableClient tableClient = account.createCloudTableClient();
            CloudTable cloudTable = tableClient.getTableReference("Sightings");
            TableQuery<SnapshotEntity> query = TableQuery.from(SnapshotEntity.class);

            for (SnapshotEntity entity : cloudTable.execute(query)) {
                snapshots.add(mapper.readValue(entity.getSnapshot(), Snapshot.class));

                cameras.add(snapshots.get(snapshots.size() - 1).getCamera().getUid());
            }

            for (String uid : cameras) {
                seriesData.add(snapshots.stream()
                        .filter(c -> uid.equals(c.getCamera().getUid()))
                        .count());
                speedingData.add(snapshots.stream()
                        .filter(c -> uid.equals(c.getCamera().getUid()) && c.isSpeeding() == true)
                        .count());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        chartData.setCameraUIDs(cameras);
        chartData.setSnapshotSeries(seriesData);
        chartData.setSpeedingSeries(speedingData);
        
        return chartData;
    }
}
