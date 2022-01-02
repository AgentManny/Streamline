package gg.manny.streamline.util;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.google.common.base.Strings.repeat;

public class NumberUtils {

    private static final Map<String, Integer> ROMAN_NUMERALS = new LinkedHashMap<>();

    private static final NavigableMap<Long, String> SUFFIXES = new TreeMap<>();
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

    static {
        ROMAN_NUMERALS.put("M", 1000);
        ROMAN_NUMERALS.put("CM", 900);
        ROMAN_NUMERALS.put("D", 500);
        ROMAN_NUMERALS.put("CD", 400);
        ROMAN_NUMERALS.put("C", 100);
        ROMAN_NUMERALS.put("XC", 90);
        ROMAN_NUMERALS.put("L", 50);
        ROMAN_NUMERALS.put("XL", 40);
        ROMAN_NUMERALS.put("X", 10);
        ROMAN_NUMERALS.put("IX", 9);
        ROMAN_NUMERALS.put("V", 5);
        ROMAN_NUMERALS.put("IV", 4);
        ROMAN_NUMERALS.put("I", 1);
    }

    public static int getRandomRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isShort(String input) {
        try {
            Short.parseShort(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isEven(int number) {
        return number % 2 == 0;
    }

    public static String convertAbbreviated(long value) {
        if (value == Long.MIN_VALUE) {
            return convertAbbreviated(Long.MIN_VALUE + 1);
        }

        if (value < 0) {
            return "-" + convertAbbreviated(-value);
        }

        if (value < 1000) {
            return Long.toString(value); //deal with easy case
        }

        Map.Entry<Long, String> e = SUFFIXES.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);

        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String formatNumber(long value) {
        return NUMBER_FORMAT.format(value);
    }

    public static String romanNumerals(int value) {
        StringBuilder res = new StringBuilder();
        for (final Map.Entry<String, Integer> entry : ROMAN_NUMERALS.entrySet()) {
            final int matches = value / entry.getValue();
            res.append(repeat(entry.getKey(), matches));
            value %= entry.getValue();
        }
        return res.toString();
    }

}