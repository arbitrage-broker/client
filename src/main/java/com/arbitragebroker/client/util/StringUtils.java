package com.arbitragebroker.client.util;

import org.springframework.util.ClassUtils;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StringUtils {
    public static List<UUID> reverseArrayFromString(String input) {
        // Split the string by commas
        List<UUID> numbers = Arrays.asList(input.split(",")).stream().map(UUID::fromString).collect(Collectors.toList());

        // Reverse the list
        Collections.reverse(numbers);

        // Join the list back into a string with commas
        return numbers;
    }
    public static String generateFilterKey(String className,String name, Object filter, Object pageable) {
        String filterString = MapperHelper.getOrDefault(()-> filter.toString(),"");
        String pageableString = MapperHelper.getOrDefault(()-> pageable.toString(),"");

        return String.format("%s:%s:%s", className, name, DigestUtils.md5DigestAsHex((filterString + pageableString).getBytes()));
    }
    public static String generateIdKey(String className,Object id) {
        return className + ":" + id.toString() + ":id";
    }

    public static String getTargetClassName(Object obj) {
        return ClassUtils.getUserClass(obj).getSimpleName();
    }

    public static String shortenText(String input) {
        if (input == null) {
            return null; // Handle null input
        }

        // Check if the string length exceeds 30 characters
        if (input.length() > 30) {
            return input.substring(0, 30) + "..";
        } else {
            return input;
        }
    }
}
