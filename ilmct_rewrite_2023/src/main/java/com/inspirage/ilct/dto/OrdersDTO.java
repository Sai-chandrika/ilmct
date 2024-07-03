package com.inspirage.ilct.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 31-10-2023
 */
public class OrdersDTO {
    private List<Order> Order = new ArrayList<>();

    public List<Order> getOrder() {
        return Order;
    }

    public void setOrder(List<Order> order) {
        Order = order;
    }

    @Override
    public String toString() {
        return "OrdersDTO{" +
                "Order=" + Order +
                '}';
    }
}
