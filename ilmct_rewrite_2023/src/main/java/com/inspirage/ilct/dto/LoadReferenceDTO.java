package com.inspirage.ilct.dto;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 31-10-2023
 */
public class LoadReferenceDTO {
    private String LoadReferenceType;

    private String content;

    public String getLoadReferenceType ()
    {
        return LoadReferenceType;
    }

    public void setLoadReferenceType (String LoadReferenceType)
    {
        this.LoadReferenceType = LoadReferenceType;
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
        return "ClassPojo [LoadReferenceType = "+LoadReferenceType+", content = "+content+"]";
    }
}
