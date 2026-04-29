package com.michele.mocks.util;

import com.michele.mocks.exception.BadRequestException;

import java.util.Arrays;

public enum MetricRange {
    DAYS_7("7d", 7),
    DAYS_30("30d", 30),
    DAYS_90("90d", 90);

    private final String code;
    private final int days;

    MetricRange(String code, int days) {
        this.code = code;
        this.days = days;
    }

    public String code() {
        return code;
    }

    public int days() {
        return days;
    }

    public static MetricRange parse(String range) {
        if (range == null || range.isBlank()) {
            return DAYS_30;
        }

        String normalized = range.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(value -> value.code.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Unsupported range. Allowed values: 7d, 30d, 90d"));
    }
}
