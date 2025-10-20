package dev.slethware.stringanalyzer.utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StringAnalyzerUtil {

    public static int calculateLength(String value) {
        return value.replaceAll("\\s+", "").length();
    }

    public static boolean isPalindrome(String value) {
        String normalized = value.toLowerCase().replaceAll("\\s+", "");
        int left = 0;
        int right = normalized.length() - 1;

        while (left < right) {
            if (normalized.charAt(left) != normalized.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    public static int countUniqueCharacters(String value) {
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : value.toLowerCase().toCharArray()) {
            uniqueChars.add(c);
        }
        return uniqueChars.size();
    }

    public static int countWords(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        return value.trim().split("\\s+").length;
    }

    public static String calculateSha256Hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public static Map<String, Integer> calculateCharacterFrequency(String value) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (char c : value.toLowerCase().toCharArray()) {
            String character = String.valueOf(c);
            frequencyMap.put(character, frequencyMap.getOrDefault(character, 0) + 1);
        }
        return frequencyMap;
    }
}