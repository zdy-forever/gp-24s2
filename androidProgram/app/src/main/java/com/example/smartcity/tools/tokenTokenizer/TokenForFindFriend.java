package com.example.smartcity.tools.tokenTokenizer;

import java.util.Objects;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */


public class TokenForFindFriend {

    /**
     * Enum representing the type of token. A token can be one of the following:
     * USERNAME, ACCOUNT, or SEMICOLON.
     */
    public enum Type {USERNAME, ACCOUNT, SEMICOLON}

    /**
     * The following exception should be thrown if a tokenizer attempts to tokenize something that is not of one
     * of the types of tokens.
     */
    public static class IllegalTokenException extends IllegalArgumentException {
        public IllegalTokenException(String errorMessage) {
            super(errorMessage);
        }
    }

    // Fields of the class Token.
    private final String token; // Token representation in String form.
    private final Type type;    // Type of the token.

    /**
     * Constructor for TokenForFindFriend.
     * Initializes the token with its string representation and its type.
     *
     * @param token The string representation of the token.
     * @param type The type of the token (USERNAME, ACCOUNT, or SEMICOLON).
     */
    public TokenForFindFriend(String token, Type type) {
        this.token = token;
        this.type = type;
    }


    /**
     * Returns the string representation of the token.
     *
     * @return The token string.
     */
    public String getToken() {
        return token;
    }


    /**
     * Returns the type of the token.
     *
     * @return The token type (USERNAME, ACCOUNT, or SEMICOLON).
     */
    public Type getType() {
        return type;
    }



    /**
     * Provides a string representation of the token object.
     * Depending on the token's type, it returns a formatted string with the token value.
     *
     * @return A formatted string showing the type and value of the token.
     */
    @Override
    public String toString() {
        if (type == Type.USERNAME) {
            return "USERNAME( " + token + " )";
        } else if (type == Type.ACCOUNT) {
            return "ACCOUNT( " + token + " )";
        }
        else
        {
            return "SEMICOLON( "+token+" )";
        }
    }


    /**
     * Compares this token with another object to check for equality.
     * Two tokens are considered equal if they have the same type and string value.
     *
     * @param other The object to be compared with this token.
     * @return true if the tokens are equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true; // Same hashcode.
        if (!(other instanceof TokenForFindFriend)) return false; // Null or not the same type.
        return this.type == ((TokenForFindFriend) other).getType() && this.token.equals(((TokenForFindFriend) other).getToken()); // Values are the same.
    }

    /**
     * Computes the hash code for the token object based on its string value and type.
     *
     * @return The hash code of the token.
     */
    @Override
    public int hashCode() {
        return Objects.hash(token, type);
    }
}
