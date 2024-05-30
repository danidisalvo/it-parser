package com.probendi.itparser;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonWriterIT {

    @Test
    void write() throws IOException {
        Set<ConsolidatedEntry> entries = new LinkedHashSet<>();
        entries.add(new ConsolidatedEntry(1, "work 1", "work 1, a", List.of("abc")));
        entries.add(new ConsolidatedEntry(2, "work 2", "work 2, a", List.of("xyz")));

        new JsonWriter().write("test.json", entries);

        Path path = Paths.get("test.json");
        List<String> lines = Files.readAllLines(path);
        assertEquals(4, lines.size(), "the file has the wrong size");
        assertEquals("{\"entries\":[", lines.get(0));
        assertEquals("{\"work\":\"work 1\",\"position\":\"work 1, a\",\"text\":\"abc\"},", lines.get(1));
        assertEquals("{\"work\":\"work 2\",\"position\":\"work 2, a\",\"text\":\"xyz\"}", lines.get(2));
        assertEquals("]}", lines.get(3));
    }
}
