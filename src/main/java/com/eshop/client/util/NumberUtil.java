package com.eshop.client.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class NumberUtil {
    public static BigDecimal getRandom(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) >= 0) {
            throw new IllegalArgumentException("Max must be greater than Min");
        }

        // Generate a random BigDecimal between 0 and 1
        BigDecimal randomBigDecimal = BigDecimal.valueOf(Math.random());

        // Scale the random value to the range (max - min) and add min
        BigDecimal range = max.subtract(min);
        BigDecimal scaled = randomBigDecimal.multiply(range).add(min);

        // Set scale if needed (e.g., 2 decimal places)
        return scaled.setScale(6, RoundingMode.HALF_UP); // Set to 2 decimal places
    }
    public static BigDecimal findClosest(List<BigDecimal> numbers, BigDecimal target) {
        return numbers.stream()
                .min((a, b) -> a.subtract(target).abs().compareTo(b.subtract(target).abs()))
                .orElseThrow(() -> new IllegalArgumentException("List is empty"));
    }
}
