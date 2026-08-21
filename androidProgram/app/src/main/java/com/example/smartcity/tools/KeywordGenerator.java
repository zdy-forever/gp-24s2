package com.example.smartcity.tools;

import java.util.ArrayList;
import java.util.List;

public class KeywordGenerator {

    public static List<String> generateKeywords(String input) {
        List<String> keywords = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return keywords;
        }
        int length = input.length();
        for (int i = 0; i < length; i++) {
            addSubstrings(input, i, keywords);
        }
        return keywords;
    }
    private static void addSubstrings(String input, int start, List<String> keywords) {
        for (int j = start + 1; j <= input.length(); j++) {
            String substring = input.substring(start, j).toLowerCase();
            keywords.add(substring);
        }
    }
}
