package com.arbitragebroker.client.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.util.ClassUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

        return String.format("%s:%s:%s", className, name, hashSHA256(filterString + pageableString));
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

    private static String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 16); // Use only first 16 chars for efficiency
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
