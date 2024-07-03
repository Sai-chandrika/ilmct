package com.inspirage.ilct.dto;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 31-10-2023
 */
public class ShipmentDateTimeDTO {
    private String TZId;

    private String DateTime;

    public String getTZId ()
    {
        return TZId;
    }

    public void setTZId (String TZId)
    {
        this.TZId = TZId;
    }

    public String getDateTime ()
    {
        return DateTime;
    }

    public void setDateTime (String DateTime)
    {
        this.DateTime = DateTime;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [TZId = "+TZId+", DateTime = "+DateTime+"]";
    }
}
