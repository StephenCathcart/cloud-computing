package uk.co.stephencathcart.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.stephencathcart.common.SmartSpeedCamera;
import uk.co.stephencathcart.common.Snapshot;

/**
 * RESTful Controller for serving data to the Query application. Contains simple
 * GET request mappings that return Json data.
 *
 * @author Stephen Cathcart
 */
@RestController
@RequestMapping(
        value = "/api",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        headers = {"content-type=application/json"})
public class QueryController {

    @Autowired
    private AzureRepository repo;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/findAllCameraRegistrations", method = RequestMethod.GET)
    public List<SmartSpeedCamera> findAllCameras() {
        return repo.findAllCameraRegistrations();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/findAllPrioritySightings", method = RequestMethod.GET)
    public List<Snapshot> findAllPrioritySightings() {
        return repo.findAllPrioritySightings();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/findSightingHistory/{registration}", method = RequestMethod.GET)
    public List<Snapshot> findSightingHistory(@PathVariable String registration) {
        return repo.findSightingHistory(registration);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/findSnapshotData", method = RequestMethod.GET)
    public ChartData findSnapshotData() {
        return repo.findSnapshotData();
    }
}
