package com.michele.mocks.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DashboardFormatters {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private DashboardFormatters() {
    }

    public static String formatInteger(long value) {
        NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
        return format.format(value);
    }

    public static String formatPercentage(BigDecimal value) {
        BigDecimal normalized = value == null ? BigDecimal.ZERO : value.setScale(1, RoundingMode.HALF_UP);
        return normalized.toPlainString() + "%";
    }

    public static String formatCurrency(BigDecimal value) {
        BigDecimal normalized = value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        return format.format(normalized);
    }

    public static String formatDate(LocalDate date) {
        return date == null ? null : date.format(DATE_FORMATTER);
    }
}
