package uk.co.stephencathcart.query;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Pojo for holding chart data for the Chartist.js graph.
 * 
 * @author Stephen Cathcart
 */
public class ChartData implements Serializable {

    private Set<String> cameraUIDs;
    private List<Long> snapshotSeries;
    private List<Long> speedingSeries;

    public Set<String> getCameraUIDs() {
        return cameraUIDs;
    }

    public void setCameraUIDs(Set<String> cameraUIDs) {
        this.cameraUIDs = cameraUIDs;
    }

    public List<Long> getSnapshotSeries() {
        return snapshotSeries;
    }

    public void setSnapshotSeries(List<Long> snapshotSeries) {
        this.snapshotSeries = snapshotSeries;
    }

    public List<Long> getSpeedingSeries() {
        return speedingSeries;
    }

    public void setSpeedingSeries(List<Long> speedingSeries) {
        this.speedingSeries = speedingSeries;
    }
}
