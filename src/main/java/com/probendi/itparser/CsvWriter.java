package com.probendi.itparser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * Writes entries to a CSV file.
 *
 * Copyright &copy; 2023-2024, Daniele Di Salvo
 *
 * @author Daniele Di Salvo
 * @since 2.0
 */
public class CsvWriter implements Writer {

    private static final String HEADER = "Work\tPosition\tText\n";

    @Override
    public void write(String file, Collection<ConsolidatedEntry> entries) throws IOException {
        String name = validate(file, entries);
        try (final FileWriter fw = new FileWriter(name);
             final BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(HEADER);
            for (ConsolidatedEntry entry : entries) {
                bw.write(entry.toCsv());
            }
        }
        System.out.printf("Written %d entries to %s%n", entries.size(), name);
    }
}
