package com.michele.mocks.dto.inventory.dashboard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum DashboardType {
    DASHBOARD("dashboard"),
    KPI("kpi"),
    DONUT("donut"),
    LINE("line"),
    BAR("bar"),
    PROGRESS("progress"),
    HEATMAP("heatmap"),
    TABLE("table");

    private final String value;

    DashboardType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static DashboardType fromValue(String raw) {
        return Arrays.stream(values())
                .filter(type -> type.value.equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported dashboard type: " + raw));
    }
}
