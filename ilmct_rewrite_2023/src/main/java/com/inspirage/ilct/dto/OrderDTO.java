package com.inspirage.ilct.dto;

import java.util.ArrayList;
import java.util.List;

public class OrderDTO {

    private List<String> OrderID = new ArrayList<String>();

    public List<String> getOrderID ()
    {
        return OrderID;
    }

    public void setOrderID (List<String> OrderID)
    {
        this.OrderID = OrderID;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [OrderID = "+OrderID+"]";
    }
}
