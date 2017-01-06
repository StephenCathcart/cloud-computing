package uk.co.stephencathcart.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Date;

/**
 * Pojo that represents a smart speed camera. Start date uses ISO 8601 format.
 *
 * @author Stephen Cathcart
 */
public class SmartSpeedCamera implements Serializable {

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Date startTime;
    private String uid;
    private String street;
    private String town;
    private Integer maxSpeedLimit;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public Integer getMaxSpeedLimit() {
        return maxSpeedLimit;
    }

    public void setMaxSpeedLimit(Integer maxSpeedLimit) {
        this.maxSpeedLimit = maxSpeedLimit;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uid", getUid())
                .add("street", getStreet())
                .add("town", getTown())
                .add("maxSpeedLimit", getMaxSpeedLimit())
                .toString();
    }
}
