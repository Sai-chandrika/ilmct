package com.inspirage.ilct.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 31-10-2023
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "FeedSource",
        "FeedGenDtTime",
        "FeedReference",

})
public class ForwarderDTO {
    private String ForwarderName;

    private String ForwarderPhone;

    private String ForwarderEmail;

    private String LocationID;

    public String getForwarderName ()
    {
        return ForwarderName;
    }

    public void setForwarderName (String ForwarderName)
    {
        this.ForwarderName = ForwarderName;
    }

    public String getForwarderPhone ()
    {
        return ForwarderPhone;
    }

    public void setForwarderPhone (String ForwarderPhone)
    {
        this.ForwarderPhone = ForwarderPhone;
    }

    public String getForwarderEmail ()
    {
        return ForwarderEmail;
    }

    public void setForwarderEmail (String ForwarderEmail)
    {
        this.ForwarderEmail = ForwarderEmail;
    }

    public String getLocationID ()
    {
        return LocationID;
    }

    public void setLocationID (String LocationID)
    {
        this.LocationID = LocationID;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ForwarderName = "+ForwarderName+", ForwarderPhone = "+ForwarderPhone+", ForwarderEmail = "+ForwarderEmail+", LocationID = "+LocationID+"]";
    }
}
