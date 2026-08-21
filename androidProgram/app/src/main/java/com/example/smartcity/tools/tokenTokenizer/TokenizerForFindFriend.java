package com.example.smartcity.tools.tokenTokenizer;
import java.util.Scanner;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */

public class TokenizerForFindFriend {
    private String buffer;
    private TokenForFindFriend currentToken;

    /**
     * Constructor for the TokenizerForFindFriend class.
     * Initializes the tokenizer with the given input text and sets the first token.
     *
     * @param text The input text to be tokenized.
     */
    public TokenizerForFindFriend(String text) {
        buffer = text;
        next();
    }

    /**
     * Advances the tokenizer to the next token in the buffer.
     * It processes the next character or string in the buffer, identifying it as a USERNAME, ACCOUNT, or SEMICOLON.
     * If no more tokens are available, it sets the current token to null.
     */
    public void next() {
        buffer = buffer.trim();

        if (buffer.isEmpty()) {
            currentToken = null;
            return;
        }

        char currentChar = buffer.charAt(0);

        if (currentChar == ';') {
            currentToken = new TokenForFindFriend(";", TokenForFindFriend.Type.SEMICOLON);
            buffer = buffer.substring(1);
        } else {
            int index = buffer.indexOf(';');
            if (index == -1) {
                index = buffer.length();
            }

            String tokenStr = buffer.substring(0, index).trim();
            buffer = buffer.substring(index);

            if (tokenStr.contains("@")) {
                currentToken = new TokenForFindFriend(tokenStr, TokenForFindFriend.Type.ACCOUNT);
            } else {
                currentToken = new TokenForFindFriend(tokenStr, TokenForFindFriend.Type.USERNAME);
            }
        }
    }

    /**
     * Returns the current token that is being processed.
     *
     * @return The current token object, or null if no more tokens are available.
     */
    public TokenForFindFriend current() {
        return currentToken;
    }

    /**
     * Checks if there are more tokens to process.
     *
     * @return true if there is a current token, false if no more tokens are available.
     */
    public boolean hasNext() {
        return currentToken != null;
    }
}

