package com.probendi.itparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ITParserTest {

    @Test
    void getWellKnownForms() {
        String[] expected = {"78","79","80","81","82","83","84","85","86","87"};
        String[] actual = ITParser.getWellKnownForms("ens");
        assertArrayEquals(expected, actual, "the arrays do not match");
    }

    @Test
    void getWellKnownFormsEmpty() {
        String[] actual = ITParser.getWellKnownForms("forma");
        assertEquals(0, actual.length, "the arrays do not match");
    }
}
