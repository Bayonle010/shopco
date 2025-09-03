package com.shopco.core.utils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class StringUtil {
    public static Set<String> parseLowerCsv(String csv) {
        if (csv == null || csv.isBlank()) return null;
        return Arrays.stream(csv.split(","))
                .map(s -> s.trim().toLowerCase())
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }
}
