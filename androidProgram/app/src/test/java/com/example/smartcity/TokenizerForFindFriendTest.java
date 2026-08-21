package com.example.smartcity;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.smartcity.tools.tokenTokenizer.TokenForFindFriend;
import com.example.smartcity.tools.tokenTokenizer.TokenizerForFindFriend;
/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */
public class TokenizerForFindFriendTest {

    @Test
    public void testSingleUsernameToken() {
        TokenizerForFindFriend tokenizer = new TokenizerForFindFriend("user123");
        assertTrue(tokenizer.hasNext());
        TokenForFindFriend token = tokenizer.current();
        assertNotNull(token);
        assertEquals("user123", token.getToken());
        assertEquals(TokenForFindFriend.Type.USERNAME, token.getType());
    }

    @Test
    public void testSingleAccountToken() {
        TokenizerForFindFriend tokenizer = new TokenizerForFindFriend("user@example.com");
        assertTrue(tokenizer.hasNext());
        TokenForFindFriend token = tokenizer.current();
        assertNotNull(token);
        assertEquals("user@example.com", token.getToken());
        assertEquals(TokenForFindFriend.Type.ACCOUNT, token.getType());
    }

    @Test
    public void testSingleSemicolonToken() {
        TokenizerForFindFriend tokenizer = new TokenizerForFindFriend(";");
        assertTrue(tokenizer.hasNext());
        TokenForFindFriend token = tokenizer.current();
        assertNotNull(token);
        assertEquals(";", token.getToken());
        assertEquals(TokenForFindFriend.Type.SEMICOLON, token.getType());
    }

    @Test
    public void testMultipleTokens() {
        TokenizerForFindFriend tokenizer = new TokenizerForFindFriend("user1 user2@example.com ;");

        // Test first token (USERNAME)
        assertTrue(tokenizer.hasNext());
        TokenForFindFriend token1 = tokenizer.current();
        assertEquals("user1", token1.getToken());
        assertEquals(TokenForFindFriend.Type.USERNAME, token1.getType());

        // Move to the next token
        tokenizer.next();
        assertTrue(tokenizer.hasNext());
        TokenForFindFriend token2 = tokenizer.current();
        assertEquals("user2@example.com", token2.getToken());
        assertEquals(TokenForFindFriend.Type.ACCOUNT, token2.getType());

        // Move to the next token
        tokenizer.next();
        assertTrue(tokenizer.hasNext());
        TokenForFindFriend token3 = tokenizer.current();
        assertEquals(";", token3.getToken());
        assertEquals(TokenForFindFriend.Type.SEMICOLON, token3.getType());

        // Move to the end
        tokenizer.next();
        assertFalse(tokenizer.hasNext());
        assertNull(tokenizer.current());
    }

    @Test
    public void testEmptyString() {
        TokenizerForFindFriend tokenizer = new TokenizerForFindFriend("");
        assertFalse(tokenizer.hasNext());
        assertNull(tokenizer.current());
    }

    @Test
    public void testWhitespaceOnly() {
        TokenizerForFindFriend tokenizer = new TokenizerForFindFriend("   ");
        assertFalse(tokenizer.hasNext());
        assertNull(tokenizer.current());
    }

    @Test
    public void testLeadingAndTrailingWhitespace() {
        TokenizerForFindFriend tokenizer = new TokenizerForFindFriend("  user@example.com  ");
        assertTrue(tokenizer.hasNext());
        TokenForFindFriend token = tokenizer.current();
        assertEquals("user@example.com", token.getToken());
        assertEquals(TokenForFindFriend.Type.ACCOUNT, token.getType());
    }

    @Test
    public void testMixedTokensWithWhitespace() {
        TokenizerForFindFriend tokenizer = new TokenizerForFindFriend(" user123 ; user@example.com ");

        // Test first token (USERNAME)
        assertTrue(tokenizer.hasNext());
        assertEquals("user123", tokenizer.current().getToken());
        assertEquals(TokenForFindFriend.Type.USERNAME, tokenizer.current().getType());

        // Move to the next token (SEMICOLON)
        tokenizer.next();
        assertTrue(tokenizer.hasNext());
        assertEquals(";", tokenizer.current().getToken());
        assertEquals(TokenForFindFriend.Type.SEMICOLON, tokenizer.current().getType());

        // Move to the next token (ACCOUNT)
        tokenizer.next();
        assertTrue(tokenizer.hasNext());
        assertEquals("user@example.com", tokenizer.current().getToken());
        assertEquals(TokenForFindFriend.Type.ACCOUNT, tokenizer.current().getType());

        // No more tokens
        tokenizer.next();
        assertFalse(tokenizer.hasNext());
        assertNull(tokenizer.current());
    }
}

