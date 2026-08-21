package com.example.smartcity.tools.expParser;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */
public class StringExp extends Exp {
    Exp exp; // UsernameExp or AccountExp
    /**
     * Constructor for StringExp.
     * Initializes the expression field with either a UsernameExp or AccountExp object.
     *
     * @param exp The expression to be stored, either a UsernameExp or AccountExp.
     */
    public StringExp(Exp exp) {
        this.exp = exp;
    }
    /**
     * Returns the string representation of the underlying expression.
     * This method delegates the `show()` call to the contained expression.
     *
     * @return A string representation of the underlying UsernameExp or AccountExp.
     */
    @Override
    public String show() {
        return exp.show();
    }
}

