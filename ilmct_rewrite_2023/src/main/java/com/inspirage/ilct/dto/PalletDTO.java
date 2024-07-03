package com.inspirage.ilct.dto;

import java.util.ArrayList;
import java.util.List;

public class PalletDTO {
    private OrderDTO Order;
    private String SourceLocationID;

    private String DestLocationID;

    private String ContainerID;

    private List<PalletContentDTO> PalletContent = new ArrayList<PalletContentDTO>();

    private String PalletCount;

    private String PalletID;

    public OrderDTO getOrder ()
    {
        return Order;
    }

    public void setOrder (OrderDTO Order)
    {
        this.Order = Order;
    }

    public String getSourceLocationID ()
    {
        return SourceLocationID;
    }

    public void setSourceLocationID (String SourceLocationID)
    {
        this.SourceLocationID = SourceLocationID;
    }

    public String getDestLocationID ()
    {
        return DestLocationID;
    }

    public void setDestLocationID (String DestLocationID)
    {
        this.DestLocationID = DestLocationID;
    }

    public String getContainerID ()
    {
        return ContainerID;
    }

    public void setContainerID (String ContainerID)
    {
        this.ContainerID = ContainerID;
    }

    public List<PalletContentDTO> getPalletContent ()
    {
        return PalletContent;
    }

    public void setPalletContent (List<PalletContentDTO> PalletContent)
    {
        this.PalletContent = PalletContent;
    }

    public String getPalletCount ()
    {
        return PalletCount;
    }

    public void setPalletCount (String PalletCount)
    {
        this.PalletCount = PalletCount;
    }

    public String getPalletID ()
    {
        return PalletID;
    }

    public void setPalletID (String PalletID)
    {
        this.PalletID = PalletID;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Order = "+Order+", SourceLocationID = "+SourceLocationID+", DestLocationID = "+DestLocationID+", ContainerID = "+ContainerID+", PalletContent = "+PalletContent+", PalletCount = "+PalletCount+", PalletID = "+PalletID+"]";
    }
}
