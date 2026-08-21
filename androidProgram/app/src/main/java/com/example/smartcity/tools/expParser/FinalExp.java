package com.example.smartcity.tools.expParser;


/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */

public class FinalExp extends Exp {
    StringExp stringExp;
    private SemicolonExp semicolonExp;
    FinalExp finalExp;

    // <finalExp> ::= <string>

    /**
     * Constructor for a FinalExp that only contains a string expression.
     * This corresponds to the grammar rule <finalExp> ::= <string>.
     *
     * @param stringExp The string expression contained in this FinalExp.
     */

    public FinalExp(StringExp stringExp) {
        this.stringExp = stringExp;
        this.semicolonExp = null;
        this.finalExp = null;
    }

    // <finalExp> ::= <string> <semicolon> <finalExp>

    /**
     * Constructor for a FinalExp that contains a string, a semicolon, and another final expression.
     * This corresponds to the grammar rule <finalExp> ::= <string> <semicolon> <finalExp>.
     *
     * @param stringExp The string expression contained in this FinalExp.
     * @param semicolonExp The semicolon expression that separates the string and final expression.
     * @param finalExp Another final expression that follows the semicolon.
     */

    public FinalExp(StringExp stringExp, SemicolonExp semicolonExp, FinalExp finalExp) {
        this.stringExp = stringExp;
        this.semicolonExp = semicolonExp;
        this.finalExp = finalExp;
    }

    // if <finalExp> ::= <string> <semicolon>
    // it is impossible because I check it in FindNewFriend.java

    @Override
    public String show() {
        if (semicolonExp == null) {
            return stringExp.show();
        } else {
            return stringExp.show() + semicolonExp.show() + finalExp.show();
        }
    }
}
