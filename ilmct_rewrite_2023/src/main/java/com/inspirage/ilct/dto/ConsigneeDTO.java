package com.inspirage.ilct.dto;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 31-10-2023
 */
public class ConsigneeDTO {
    private String ConsigneePhone;

    private String ConsigneeName;

    private String LocationID;

    private String ConsigneeEmail;

    public String getConsigneePhone ()
    {
        return ConsigneePhone;
    }

    public void setConsigneePhone (String ConsigneePhone)
    {
        this.ConsigneePhone = ConsigneePhone;
    }

    public String getConsigneeName ()
    {
        return ConsigneeName;
    }

    public void setConsigneeName (String ConsigneeName)
    {
        this.ConsigneeName = ConsigneeName;
    }

    public String getLocationID ()
    {
        return LocationID;
    }

    public void setLocationID (String LocationID)
    {
        this.LocationID = LocationID;
    }

    public String getConsigneeEmail ()
    {
        return ConsigneeEmail;
    }

    public void setConsigneeEmail (String ConsigneeEmail)
    {
        this.ConsigneeEmail = ConsigneeEmail;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ConsigneePhone = "+ConsigneePhone+", ConsigneeName = "+ConsigneeName+", LocationID = "+LocationID+", ConsigneeEmail = "+ConsigneeEmail+"]";
    }
}
