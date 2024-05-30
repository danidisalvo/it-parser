package com.probendi.itparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntryTest {

    @Test
    void parseEntry() {
        String line = "<p title=\"Super Sent., lib. 1 q. 1 a. 2 ad 2.\">" +
                "<span class=\"caseNumber\">Case 1.&nbsp;</span>" +
                "<span class=\"ref\">" +
                "<span class=\"placeNumber\">Place 2.&nbsp;</span>" +
                "Super Sent., lib. 1 q. 1 a. 2 ad 2.&nbsp;</span>" +
                "[...]" +
                "<sup><font size=\"-1\">-1</font></sup>" +
                "&nbsp;some text" +
                "</p>";
        Entry expected = new Entry(1, 2,"Super Sent.",
                "Super Sent., lib. 1 q. 1 a. 2 ad 2.", "[...]-1 some text");
        Entry actual = Entry.parseEntry(line);
        assertEquals(expected, actual, "entries do not match");
    }
}
