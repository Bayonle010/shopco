package com.shopco.enums;

import com.shopco.core.exception.IllegalArgumentException;

import java.util.Arrays;

public enum Category {
    FORMAL, GYM, PARTY, CASUAL;

    public static Category fromString(String value) {
        if (value == null) return null;

        for (Category category : Category.values()) {
            if (category.name().equalsIgnoreCase(value.trim())) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + value +
                ". Valid values are: " + Arrays.toString(Category.values()));

    }

}
