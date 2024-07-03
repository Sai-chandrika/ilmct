package com.inspirage.ilct.dto;

public class MaterialReferenceDTO {

    private String MaterialReferenceType;

    private String content;

    public String getMaterialReferenceType ()
    {
        return MaterialReferenceType;
    }

    public void setMaterialReferenceType (String MaterialReferenceType)
    {
        this.MaterialReferenceType = MaterialReferenceType;
    }

    public String getContent ()
    {
        return content;
    }

    public void setContent (String content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [MaterialReferenceType = "+MaterialReferenceType+", content = "+content+"]";
    }
}
