package com.shopco.product.enums;

import com.shopco.core.exception.IllegalArgumentException;

import java.util.Arrays;

public enum Size {
    S,M,L,XL,XXL;

    public static Size fromString(String value) {
        if (value == null || value.equalsIgnoreCase("all")) return null;

        for (Size size : Size.values()) {
            if (size.name().equalsIgnoreCase(value.trim())) {
                return size;
            }
        }
        throw new IllegalArgumentException("Invalid size: " + value +
                ". Valid values are: " + Arrays.toString(Size.values()));
    }
}
