package com.example.smartcity;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.smartcity.tools.expParser.SemicolonExp;

public class SemicolonExpTest {

    @Test
    public void testShow() {
        SemicolonExp semicolonExp = new SemicolonExp();
        assertEquals(";", semicolonExp.show());
    }
}

