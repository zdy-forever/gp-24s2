package com.example.smartcity;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.smartcity.tools.tokenTokenizer.TokenForFindFriend;
/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */
public class TokenForFindFriendTest {

    @Test
    public void testTokenForUsername() {
        TokenForFindFriend token = new TokenForFindFriend("user123", TokenForFindFriend.Type.USERNAME);

        assertEquals("user123", token.getToken());
        assertEquals(TokenForFindFriend.Type.USERNAME, token.getType());
        assertEquals("USERNAME( user123 )", token.toString());
    }

    @Test
    public void testTokenForAccount() {
        TokenForFindFriend token = new TokenForFindFriend("account456", TokenForFindFriend.Type.ACCOUNT);

        assertEquals("account456", token.getToken());
        assertEquals(TokenForFindFriend.Type.ACCOUNT, token.getType());
        assertEquals("ACCOUNT( account456 )", token.toString());
    }

    @Test
    public void testTokenForSemicolon() {
        TokenForFindFriend token = new TokenForFindFriend(";", TokenForFindFriend.Type.SEMICOLON);

        assertEquals(";", token.getToken());
        assertEquals(TokenForFindFriend.Type.SEMICOLON, token.getType());
        assertEquals("SEMICOLON( ; )", token.toString());
    }

    @Test
    public void testEqualsSameToken() {
        TokenForFindFriend token1 = new TokenForFindFriend("user123", TokenForFindFriend.Type.USERNAME);
        TokenForFindFriend token2 = new TokenForFindFriend("user123", TokenForFindFriend.Type.USERNAME);

        assertTrue(token1.equals(token2));
        assertTrue(token2.equals(token1));
    }

    @Test
    public void testEqualsDifferentToken() {
        TokenForFindFriend token1 = new TokenForFindFriend("user123", TokenForFindFriend.Type.USERNAME);
        TokenForFindFriend token2 = new TokenForFindFriend("user456", TokenForFindFriend.Type.USERNAME);

        assertFalse(token1.equals(token2));
    }

    @Test
    public void testEqualsDifferentType() {
        TokenForFindFriend token1 = new TokenForFindFriend("user123", TokenForFindFriend.Type.USERNAME);
        TokenForFindFriend token2 = new TokenForFindFriend("user123", TokenForFindFriend.Type.ACCOUNT);

        assertFalse(token1.equals(token2));
    }

    @Test
    public void testHashCodeSameToken() {
        TokenForFindFriend token1 = new TokenForFindFriend("user123", TokenForFindFriend.Type.USERNAME);
        TokenForFindFriend token2 = new TokenForFindFriend("user123", TokenForFindFriend.Type.USERNAME);

        assertEquals(token1.hashCode(), token2.hashCode());
    }

    @Test
    public void testHashCodeDifferentToken() {
        TokenForFindFriend token1 = new TokenForFindFriend("user123", TokenForFindFriend.Type.USERNAME);
        TokenForFindFriend token2 = new TokenForFindFriend("user456", TokenForFindFriend.Type.USERNAME);

        assertNotEquals(token1.hashCode(), token2.hashCode());
    }

    @Test
    public void testIllegalTokenException() {
        try {
            throw new TokenForFindFriend.IllegalTokenException("Invalid token type");
        } catch (TokenForFindFriend.IllegalTokenException e) {

        }
    }
}

