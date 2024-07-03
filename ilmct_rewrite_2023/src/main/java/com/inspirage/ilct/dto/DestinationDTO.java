package com.inspirage.ilct.dto;

/**
 * @author hari
 * @ProjectName ilct
 * @since 31-10-2023
 */
public class DestinationDTO {
    private String DestLocationID;

    public String getDestLocationID ()
    {
        return DestLocationID;
    }

    public void setDestLocationID (String DestLocationID)
    {
        this.DestLocationID = DestLocationID;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [DestLocationID = "+DestLocationID+"]";
    }
}
