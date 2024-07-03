package com.inspirage.ilct.dto;

public class PalletContentDTO {

    private String PalletId;

    private String ProductName;

    private String NetVolume;

    private MaterialReferenceDTO[] MaterialReference;

    private String NetWeightUOM;

    private String NetVolumeUOM;

    private String Quantity;

    private String ProductID;

    private String NetWeight;

    public String getProductName ()
    {
        return ProductName;
    }

    public void setProductName (String ProductName)
    {
        this.ProductName = ProductName;
    }

    public String getNetVolume ()
    {
        return NetVolume;
    }

    public void setNetVolume (String NetVolume)
    {
        this.NetVolume = NetVolume;
    }

    public MaterialReferenceDTO[] getMaterialReference ()
    {
        return MaterialReference;
    }

    public void setMaterialReference (MaterialReferenceDTO[] MaterialReference)
    {
        this.MaterialReference = MaterialReference;
    }

    public String getNetWeightUOM ()
    {
        return NetWeightUOM;
    }

    public void setNetWeightUOM (String NetWeightUOM)
    {
        this.NetWeightUOM = NetWeightUOM;
    }

    public String getNetVolumeUOM ()
    {
        return NetVolumeUOM;
    }

    public void setNetVolumeUOM (String NetVolumeUOM)
    {
        this.NetVolumeUOM = NetVolumeUOM;
    }

    public String getQuantity ()
    {
        return Quantity;
    }

    public void setQuantity (String Quantity)
    {
        this.Quantity = Quantity;
    }

    public String getProductID ()
    {
        return ProductID;
    }

    public void setProductID (String ProductID)
    {
        this.ProductID = ProductID;
    }

    public String getNetWeight ()
    {
        return NetWeight;
    }

    public void setNetWeight (String NetWeight)
    {
        this.NetWeight = NetWeight;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ProductName = "+ProductName+", NetVolume = "+NetVolume+", MaterialReference = "+MaterialReference+", NetWeightUOM = "+NetWeightUOM+", NetVolumeUOM = "+NetVolumeUOM+", Quantity = "+Quantity+", ProductID = "+ProductID+", NetWeight = "+NetWeight+"]";
    }
}
