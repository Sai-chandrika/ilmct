package com.inspirage.ilct.dto;

import lombok.Data;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 31-10-2023
 */
@Data
public class LoadMeasureDTO {
    private String TotalStops;

    private String TotalWeight;

    private String TotalOrders;

    private String TotalWeightUOM;

    private String TruckType;

    private String WeightUtilization;

    private String VolumeUtilization;

    private String ERUUtilization;

    private String TotalVolume;

    private String TotalVolumeUOM;

    private String PalletCount;

    private String MaterialCount;

    public String getTotalStops ()
    {
        return TotalStops;
    }

    public void setTotalStops (String TotalStops)
    {
        this.TotalStops = TotalStops;
    }

    public String getTotalWeight ()
    {
        return TotalWeight;
    }

    public void setTotalWeight (String TotalWeight)
    {
        this.TotalWeight = TotalWeight;
    }

    public String getTotalOrders ()
    {
        return TotalOrders;
    }

    public void setTotalOrders (String TotalOrders)
    {
        this.TotalOrders = TotalOrders;
    }

    public String getTotalWeightUOM ()
    {
        return TotalWeightUOM;
    }

    public void setTotalWeightUOM (String TotalWeightUOM)
    {
        this.TotalWeightUOM = TotalWeightUOM;
    }

    public String getTruckType ()
    {
        return TruckType;
    }

    public void setTruckType (String TruckType)
    {
        this.TruckType = TruckType;
    }

    public String getWeightUtilization ()
    {
        return WeightUtilization;
    }

    public void setWeightUtilization (String WeightUtilization)
    {
        this.WeightUtilization = WeightUtilization;
    }

    public String getVolumeUtilization ()
    {
        return VolumeUtilization;
    }

    public void setVolumeUtilization (String VolumeUtilization)
    {
        this.VolumeUtilization = VolumeUtilization;
    }

    public String getERUUtilization ()
    {
        return ERUUtilization;
    }

    public void setERUUtilization (String ERUUtilization)
    {
        this.ERUUtilization = ERUUtilization;
    }

    public String getTotalVolume ()
    {
        return TotalVolume;
    }

    public void setTotalVolume (String TotalVolume)
    {
        this.TotalVolume = TotalVolume;
    }

    public String getTotalVolumeUOM ()
    {
        return TotalVolumeUOM;
    }

    public void setTotalVolumeUOM (String TotalVolumeUOM)
    {
        this.TotalVolumeUOM = TotalVolumeUOM;
    }

    public String getPalletCount ()
    {
        return PalletCount;
    }

    public void setPalletCount (String PalletCount)
    {
        this.PalletCount = PalletCount;
    }

    public String getMaterialCount ()
    {
        return MaterialCount;
    }

    public void setMaterialCount (String MaterialCount)
    {
        this.MaterialCount = MaterialCount;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [TotalStops = "+TotalStops+", TotalWeight = "+TotalWeight+", TotalOrders = "+TotalOrders+", TotalWeightUOM = "+TotalWeightUOM+", TruckType = "+TruckType+", WeightUtilization = "+WeightUtilization+", VolumeUtilization = "+VolumeUtilization+", ERUUtilization = "+ERUUtilization+", TotalVolume = "+TotalVolume+", TotalVolumeUOM = "+TotalVolumeUOM+", PalletCount = "+PalletCount+", MaterialCount = "+MaterialCount+"]";
    }
}
