package uk.co.stephencathcart.vehiclecheck;

import javax.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Crud Repository to operate on VehicleCheck objects. No need to write
 * save / update / delete functions as this is done automatically.
 *
 * @author Stephen Cathcart
 */
@Repository
@Transactional
public interface VehicleCheckRepository extends CrudRepository<VehicleCheck, Long> {
}
