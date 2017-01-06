package uk.co.stephencathcart.vehiclecheck;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * JPA Entity class for auditing vehicle checks to a SQL database.
 *
 * @author Stephen Cathcart
 */
@Entity
@Table(name = "vehiclechecks")
public class VehicleCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String registration;

    @NotNull
    private boolean stolen;

    @NotNull
    @Column(name = "checked_date")
    private Date checkedDate;
    
    public VehicleCheck() {
    }

    public VehicleCheck(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public boolean isStolen() {
        return stolen;
    }

    public void setStolen(boolean stolen) {
        this.stolen = stolen;
    }

    public Date getCheckedDate() {
        return checkedDate;
    }

    public void setCheckedDate(Date checkedDate) {
        this.checkedDate = checkedDate;
    }


}
