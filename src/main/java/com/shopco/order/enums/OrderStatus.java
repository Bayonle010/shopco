package com.shopco.order.enums;

import com.shopco.core.exception.IllegalArgumentException;

import java.util.Arrays;

public enum OrderStatus {
    PENDING("Pending"), SHIPPED ("Shipped"), COMPLETED ("Completed");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription(){return description;}

    public static OrderStatus fromString(String value){
        if (value == null || value.equalsIgnoreCase("all")) return null;

        for (OrderStatus category : OrderStatus.values()) {
            if (category.name().equalsIgnoreCase(value.trim())) {
                return category;
            }
        }

        throw new IllegalArgumentException("Invalid category: " + value +
                ". Valid values are: " + Arrays.toString(OrderStatus.values()));


    }

}
