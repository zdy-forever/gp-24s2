package com.example.smartcity;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.smartcity.tools.expParser.AccountExp;
import com.example.smartcity.tools.expParser.FinalExp;
import com.example.smartcity.tools.expParser.SemicolonExp;
import com.example.smartcity.tools.expParser.StringExp;
import com.example.smartcity.tools.expParser.UsernameExp;

public class FinalExpTest {

    @Test
    public void testSingleStringExp() {
        UsernameExp usernameExp = new UsernameExp("testUsername");
        StringExp stringExp = new StringExp(usernameExp);
        FinalExp finalExp = new FinalExp(stringExp, null, null);
        assertEquals("testUsername", finalExp.show());
        UsernameExp usernameExp1 = new UsernameExp("testAccount@gamil.com");
        StringExp stringExp1 = new StringExp(usernameExp1);
        FinalExp finalExp1 = new FinalExp(stringExp1, null, null);
        assertEquals("testAccount@gamil.com", finalExp1.show());

    }

    @Test
    public void testStringExpWithSemicolon() {
        UsernameExp usernameExp1 = new UsernameExp("testUsername1");
        StringExp stringExp1 = new StringExp(usernameExp1);
        SemicolonExp semicolonExp = new SemicolonExp();
        AccountExp accountExp = new AccountExp("testAccount@gamil.com");
        StringExp stringExp2 = new StringExp(accountExp);
        FinalExp finalExp2 = new FinalExp(stringExp2, null, null);
        FinalExp finalExp1 = new FinalExp(stringExp1, semicolonExp, finalExp2);
        assertEquals("testUsername1;testAccount@gamil.com", finalExp1.show());
    }

    @Test
    public void testTwoSemicolons() {
        UsernameExp usernameExp1 = new UsernameExp("testUsername1");
        StringExp stringExp1 = new StringExp(usernameExp1);
        SemicolonExp semicolonExp = new SemicolonExp();
        UsernameExp usernameExp2 = new UsernameExp("testUsername2@anu.edu.au");
        StringExp stringExp2 = new StringExp(usernameExp2);
        AccountExp accountExp = new AccountExp("testAccount@gamil.com");
        StringExp stringExp3 = new StringExp(accountExp);
        FinalExp finalExp1 = new FinalExp(stringExp3, null, null);
        FinalExp finalExp2 = new FinalExp(stringExp2, semicolonExp, finalExp1);
        FinalExp finalExp3 = new FinalExp(stringExp1, semicolonExp, finalExp2);
        assertEquals("testAccount@gamil.com", finalExp1.show());
        assertEquals("testUsername2@anu.edu.au;testAccount@gamil.com", finalExp2.show());
        assertEquals("testUsername1;testUsername2@anu.edu.au;testAccount@gamil.com", finalExp3.show());

    }
}

