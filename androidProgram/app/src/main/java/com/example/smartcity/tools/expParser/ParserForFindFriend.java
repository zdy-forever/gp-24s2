package com.example.smartcity.tools.expParser;

import android.util.Log;

import com.example.smartcity.tools.tokenTokenizer.TokenForFindFriend;
import com.example.smartcity.tools.tokenTokenizer.TokenizerForFindFriend;


import com.example.smartcity.FindNewFriend;


/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */
public class ParserForFindFriend {
    private TokenizerForFindFriend tokenizer;
    private TokenForFindFriend currentToken;

    /*
     * <finalExp> ::= <string> | <string> <semicolon> <finalExp>
     * <string> ::= <username> | <account>
     * <semicolon> ::= ";"
     * <username> ::= <username>
     * <account> ::= <account>
     **/
    static int count_this_time = 0;
    static String gender;
    static int age_limit_min=-1;
    static int age_limit_max=-1;


    /**
     * Choose gender
     * @param s
     */
    public static void ChooseGender(String s) {
        gender = s;
    }

    /**
     * Get gender
     * @return
     */
    public static String getGender() {
        Log.d("Use_gender_filter","target gender is:"+gender);
        return gender;
    }

    /**
     * Choose age range
     * @param min
     * @param max
     */

    public static void ChooseAgeRange(int min, int max)
    {
        age_limit_max=max;
        age_limit_min=min;
    }



    public ParserForFindFriend(String input) {
        tokenizer = new TokenizerForFindFriend(input);
        currentToken = tokenizer.current();
    }

    private void eat(TokenForFindFriend.Type type) {
        if (currentToken != null && currentToken.getType() == type) {
            tokenizer.next();
            currentToken = tokenizer.current();
        } else {
            throw new RuntimeException("Unexpected token: " + currentToken);
        }
    }

    public FinalExp parse() {
        return finalExp();
    }

    // <finalExp> ::= <string> | <string> <semicolon> <finalExp>
    private FinalExp finalExp() {
        StringExp strExp = string();

        if (currentToken != null && currentToken.getType() == TokenForFindFriend.Type.SEMICOLON) {
            SemicolonExp semicolonExp = semicolon();
            FinalExp nextFinalExp = finalExp();
            return new FinalExp(strExp, semicolonExp, nextFinalExp);
        } else {
            return new FinalExp(strExp);
        }
    }

    // <string> ::= <username> | <account>
    private StringExp string() {
        if (currentToken.getType() == TokenForFindFriend.Type.USERNAME) {
            UsernameExp usernameExp = new UsernameExp(currentToken.getToken());
            eat(TokenForFindFriend.Type.USERNAME);
            return new StringExp(usernameExp);
        } else if (currentToken.getType() == TokenForFindFriend.Type.ACCOUNT) {
            AccountExp accountExp = new AccountExp(currentToken.getToken());
            eat(TokenForFindFriend.Type.ACCOUNT);
            return new StringExp(accountExp);
        } else {
            throw new RuntimeException("Expected USERNAME or ACCOUNT, but got: " + currentToken);
        }
    }

    // <semicolon> ::= ";"
    private SemicolonExp semicolon() {
        eat(TokenForFindFriend.Type.SEMICOLON);
        return new SemicolonExp();
    }
}


