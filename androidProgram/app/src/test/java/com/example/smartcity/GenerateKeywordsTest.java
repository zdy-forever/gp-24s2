package com.example.smartcity;


import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

import com.example.smartcity.tools.KeywordGenerator;
import com.example.smartcity.tools.User;


public class GenerateKeywordsTest {

    @Test
    public void testGenerateKeywordsWithEmptyString() {
        List<String> result = KeywordGenerator.generateKeywords("");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGenerateKeywordsWithEmptyString1() {
        List<String> result = KeywordGenerator.generateKeywords(" ");
        assertTrue(result.contains(" "));
    }

    @Test
    public void testGenerateKeywordsWithEmptyString2() {
        List<String> result = KeywordGenerator.generateKeywords(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGenerateKeywordsWithSingleCharacter() {
        List<String> result = KeywordGenerator.generateKeywords("a");
        assertEquals(1, result.size());
        assertTrue(result.contains("a"));
    }

    @Test
    public void testGenerateKeywordsWithSingleCharacter1() {
        List<String> result = KeywordGenerator.generateKeywords("1");
        assertEquals(1, result.size());
        assertTrue(result.contains("1"));
    }

    @Test
    public void testGenerateKeywordsWithSingleCharacter2() {
        List<String> result = KeywordGenerator.generateKeywords("@");
        assertEquals(1, result.size());
        assertTrue(result.contains("@"));
    }

    @Test
    public void testGenerateKeywordsWithSingleCharacter3() {
        List<String> result = KeywordGenerator.generateKeywords("你");
        assertEquals(1, result.size());
        assertTrue(result.contains("你"));
    }

    @Test
    public void testGenerateKeywordsWithMultipleCharacters() {
        List<String> result = KeywordGenerator.generateKeywords("abc");
        assertEquals(6, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("ab"));
        assertTrue(result.contains("abc"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("bc"));
        assertTrue(result.contains("c"));
    }

    @Test
    public void testGenerateKeywordsWithUpperCaseCharacters() {
        List<String> result = KeywordGenerator.generateKeywords("Ab1");
        assertEquals(6, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("ab"));
        assertTrue(result.contains("ab1"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("b1"));
        assertTrue(result.contains("1"));
    }

    @Test
    public void testGenerateKeywordsWithSpecialCharacters() {
        List<String> result = KeywordGenerator.generateKeywords("a@你");
        assertEquals(6, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("a@"));
        assertTrue(result.contains("a@你"));
        assertTrue(result.contains("@"));
        assertTrue(result.contains("@你"));
        assertTrue(result.contains("你"));
    }
}

