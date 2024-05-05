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
        Set<Entry> entries = new LinkedHashSet<>();
        entries.add(new Entry(1, 2, "work 1", "work 1, a", "abc"));
        entries.add(new Entry(3, 4, "work 2", "work 2, a", "xyz"));

        new CsvWriter().write("test.csv", entries);

        Path path = Paths.get("test.csv");
        List<String> lines = Files.readAllLines(path);
        assertEquals(3, lines.size(), "the file has the wrong size");
        assertEquals("Case\tPlace\tWork\tPosition\tText", lines.get(0));
        assertEquals("1\t2\twork 1\twork 1, a\tabc", lines.get(1));
        assertEquals("3\t4\twork 2\twork 2, a\txyz", lines.get(2));
    }
}
