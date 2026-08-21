package com.example.smartcity;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.smartcity.tools.expParser.AccountExp;
import com.example.smartcity.tools.expParser.StringExp;
import com.example.smartcity.tools.expParser.UsernameExp;

public class StringExpTest {

    @Test
    public void testShowWithUsername() {
        UsernameExp usernameExp = new UsernameExp("testUsername");
        StringExp stringExp = new StringExp(usernameExp);
        assertEquals("testUsername", stringExp.show());
    }

    @Test
    public void testShowWithAccount() {
        AccountExp accountExp = new AccountExp("testAccount@gamil.com");
        StringExp stringExp = new StringExp(accountExp);
        assertEquals("testAccount@gamil.com", stringExp.show());
    }
}

