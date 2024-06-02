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

class CsvWriterIT {

    @Test
    void write() throws IOException {
        Set<ConsolidatedEntry> entries = new LinkedHashSet<>();
        entries.add(new ConsolidatedEntry(1, "work 1", "work 1, a", Set.of("abc")));
        entries.add(new ConsolidatedEntry(2, "work 2", "work 2, a", Set.of("xyz")));

        new CsvWriter().write("test.csv", entries);

        Path path = Paths.get("test.csv");
        List<String> lines = Files.readAllLines(path);
        assertEquals(3, lines.size(), "the file has the wrong size");
        assertEquals("Work\tPosition\tText", lines.get(0));
        assertEquals("work 1\twork 1, a\tabc", lines.get(1));
        assertEquals("work 2\twork 2, a\txyz", lines.get(2));
    }
}
