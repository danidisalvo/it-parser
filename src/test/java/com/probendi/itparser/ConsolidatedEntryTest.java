package com.probendi.itparser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsolidatedEntryTest {

    @Test
    void toCsv() {
        ConsolidatedEntry entry = new ConsolidatedEntry(2, "work", "position", List.of("text"));
        String expected = "work\tposition\ttext\n";
        String actual = entry.toCsv();
        assertEquals(expected, actual, "lines do not match");
    }

    @Test
    void toJson() {
        ConsolidatedEntry entry = new ConsolidatedEntry(2, "my-work", "my-position", List.of("my-text"));
        String expected = "{\"work\":\"my-work\",\"position\":\"my-position\",\"text\":\"my-text\"}";
        String actual = entry.toJson();
        assertEquals(expected, actual, "lines do not match");
    }
}
