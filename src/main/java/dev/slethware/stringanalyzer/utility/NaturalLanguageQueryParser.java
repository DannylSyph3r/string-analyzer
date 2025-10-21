package dev.slethware.stringanalyzer.utility;

import dev.slethware.stringanalyzer.exception.BadRequestException;
import dev.slethware.stringanalyzer.exception.UnprocessableEntityException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NaturalLanguageQueryParser {

    private NaturalLanguageQueryParser() {}

    public static Map<String, Object> parseQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new BadRequestException("Query parameter cannot be empty");
        }

        String normalizedQuery = query.toLowerCase().trim();
        Map<String, Object> filters = new HashMap<>();

        try {
            // Parse word count
            parseWordCount(normalizedQuery, filters);

            // Parse palindrome
            parsePalindrome(normalizedQuery, filters);

            // Parse length constraints
            parseLength(normalizedQuery, filters);

            // Parse character contains
            parseContainsCharacter(normalizedQuery, filters);

            // Validate that we parsed at least one filter
            if (filters.isEmpty()) {
                throw new BadRequestException("Unable to parse natural language query into valid filters");
            }

            // Validate for conflicting filters
            validateFilters(filters);

            return filters;

        } catch (BadRequestException | UnprocessableEntityException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Unable to parse natural language query: " + e.getMessage());
        }
    }

    private static void parseWordCount(String query, Map<String, Object> filters) {
        // Match patterns like "single word", "one word", "two words", "three words", etc.
        Pattern singleWordPattern = Pattern.compile("\\b(single|one)\\s+word\\b");
        Pattern multiWordPattern = Pattern.compile("\\b(two|three|four|five|six|seven|eight|nine|ten)\\s+words?\\b");
        Pattern numericWordPattern = Pattern.compile("\\b(\\d+)\\s+words?\\b");

        if (singleWordPattern.matcher(query).find()) {
            filters.put("word_count", 1);
        } else {
            Matcher multiMatcher = multiWordPattern.matcher(query);
            if (multiMatcher.find()) {
                String number = multiMatcher.group(1);
                filters.put("word_count", wordToNumber(number));
            } else {
                Matcher numericMatcher = numericWordPattern.matcher(query);
                if (numericMatcher.find()) {
                    filters.put("word_count", Integer.parseInt(numericMatcher.group(1)));
                }
            }
        }
    }

    private static void parsePalindrome(String query, Map<String, Object> filters) {
        // Match patterns like "palindrome", "palindromic"
        if (query.matches(".*\\bpalindrom(e|ic|es)\\b.*")) {
            filters.put("is_palindrome", true);
        }
    }

    private static void parseLength(String query, Map<String, Object> filters) {
        // Match patterns like "longer than X", "shorter than X", "at least X", "at most X"
        Pattern longerThanPattern = Pattern.compile("\\blonger\\s+than\\s+(\\d+)\\b");
        Pattern shorterThanPattern = Pattern.compile("\\bshorter\\s+than\\s+(\\d+)\\b");
        Pattern atLeastPattern = Pattern.compile("\\bat\\s+least\\s+(\\d+)\\s+(characters?|chars?)\\b");
        Pattern atMostPattern = Pattern.compile("\\bat\\s+most\\s+(\\d+)\\s+(characters?|chars?)\\b");
        Pattern exactLengthPattern = Pattern.compile("\\bexactly\\s+(\\d+)\\s+(characters?|chars?)\\b");
        Pattern minLengthPattern = Pattern.compile("\\bmin(imum)?\\s+length\\s+(\\d+)\\b");
        Pattern maxLengthPattern = Pattern.compile("\\bmax(imum)?\\s+length\\s+(\\d+)\\b");

        Matcher longerMatcher = longerThanPattern.matcher(query);
        if (longerMatcher.find()) {
            int length = Integer.parseInt(longerMatcher.group(1));
            filters.put("min_length", length + 1);
        }

        Matcher shorterMatcher = shorterThanPattern.matcher(query);
        if (shorterMatcher.find()) {
            int length = Integer.parseInt(shorterMatcher.group(1));
            filters.put("max_length", length - 1);
        }

        Matcher atLeastMatcher = atLeastPattern.matcher(query);
        if (atLeastMatcher.find()) {
            filters.put("min_length", Integer.parseInt(atLeastMatcher.group(1)));
        }

        Matcher atMostMatcher = atMostPattern.matcher(query);
        if (atMostMatcher.find()) {
            filters.put("max_length", Integer.parseInt(atMostMatcher.group(1)));
        }

        Matcher exactMatcher = exactLengthPattern.matcher(query);
        if (exactMatcher.find()) {
            int length = Integer.parseInt(exactMatcher.group(1));
            filters.put("min_length", length);
            filters.put("max_length", length);
        }

        Matcher minMatcher = minLengthPattern.matcher(query);
        if (minMatcher.find()) {
            filters.put("min_length", Integer.parseInt(minMatcher.group(2)));
        }

        Matcher maxMatcher = maxLengthPattern.matcher(query);
        if (maxMatcher.find()) {
            filters.put("max_length", Integer.parseInt(maxMatcher.group(2)));
        }
    }

    private static void parseContainsCharacter(String query, Map<String, Object> filters) {
        // Match patterns like "containing letter x", "contains character y", "with the letter z"
        Pattern letterPattern = Pattern.compile("\\bcontain(ing|s)?\\s+(the\\s+)?(letter|character)\\s+([a-z])\\b");
        Pattern withLetterPattern = Pattern.compile("\\bwith\\s+(the\\s+)?letter\\s+([a-z])\\b");
        Pattern firstVowelPattern = Pattern.compile("\\b(first|1st)\\s+vowel\\b");
        Pattern secondVowelPattern = Pattern.compile("\\b(second|2nd)\\s+vowel\\b");  // ADD THIS
        Pattern thirdVowelPattern = Pattern.compile("\\b(third|3rd)\\s+vowel\\b");    // ADD THIS
        Pattern fourthVowelPattern = Pattern.compile("\\b(fourth|4th)\\s+vowel\\b");  // ADD THIS
        Pattern lastVowelPattern = Pattern.compile("\\b(last|5th|fifth)\\s+vowel\\b");
        Pattern vowelPattern = Pattern.compile("\\bvowel\\s+([a-z])\\b");

        Matcher letterMatcher = letterPattern.matcher(query);
        if (letterMatcher.find()) {
            filters.put("contains_character", letterMatcher.group(4));
        }

        Matcher withLetterMatcher = withLetterPattern.matcher(query);
        if (withLetterMatcher.find()) {
            filters.put("contains_character", withLetterMatcher.group(2));
        }

        Matcher firstVowelMatcher = firstVowelPattern.matcher(query);
        if (firstVowelMatcher.find()) {
            filters.put("contains_character", "a");
        }

        Matcher secondVowelMatcher = secondVowelPattern.matcher(query);
        if (secondVowelMatcher.find()) {
            filters.put("contains_character", "e");
        }

        Matcher thirdVowelMatcher = thirdVowelPattern.matcher(query);
        if (thirdVowelMatcher.find()) {
            filters.put("contains_character", "i");
        }

        Matcher fourthVowelMatcher = fourthVowelPattern.matcher(query);
        if (fourthVowelMatcher.find()) {
            filters.put("contains_character", "o");
        }

        Matcher lastVowelMatcher = lastVowelPattern.matcher(query);
        if (lastVowelMatcher.find()) {
            filters.put("contains_character", "u");
        }

        Matcher vowelMatcher = vowelPattern.matcher(query);
        if (vowelMatcher.find()) {
            filters.put("contains_character", vowelMatcher.group(1));
        }
    }

    private static void validateFilters(Map<String, Object> filters) {
        // Check for conflicting length constraints
        if (filters.containsKey("min_length") && filters.containsKey("max_length")) {
            Integer minLength = (Integer) filters.get("min_length");
            Integer maxLength = (Integer) filters.get("max_length");
            if (minLength > maxLength) {
                throw new UnprocessableEntityException(
                        "Query parsed but resulted in conflicting filters: min_length (" +
                                minLength + ") cannot be greater than max_length (" + maxLength + ")"
                );
            }
        }

        // Validate negative values
        if (filters.containsKey("min_length") && (Integer) filters.get("min_length") < 0) {
            throw new UnprocessableEntityException("Query parsed but resulted in invalid filter: min_length cannot be negative");
        }
        if (filters.containsKey("max_length") && (Integer) filters.get("max_length") < 0) {
            throw new UnprocessableEntityException("Query parsed but resulted in invalid filter: max_length cannot be negative");
        }
        if (filters.containsKey("word_count") && (Integer) filters.get("word_count") < 0) {
            throw new UnprocessableEntityException("Query parsed but resulted in invalid filter: word_count cannot be negative");
        }
    }

    private static int wordToNumber(String word) {
        return switch (word.toLowerCase()) {
            case "one" -> 1;
            case "two" -> 2;
            case "three" -> 3;
            case "four" -> 4;
            case "five" -> 5;
            case "six" -> 6;
            case "seven" -> 7;
            case "eight" -> 8;
            case "nine" -> 9;
            case "ten" -> 10;
            default -> throw new BadRequestException("Unable to parse word number: " + word);
        };
    }
}