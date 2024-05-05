package com.probendi.itparser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Writes entries to a JSON file.
 *
 * Copyright &copy; 2023-2024, Daniele Di Salvo
 *
 * @author Daniele Di Salvo
 * @since 2.0
 */
public class JsonWriter implements Writer {

    @Override
    public void write(String file, Set<Entry> entries) throws IOException {
        String name = validate(file, entries);
        boolean first = true;
        try (final FileWriter fw = new FileWriter(name);
             final BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("{\"entries\":[\n");
            for (final Entry entry : entries) {
                if (!first) {
                    bw.write(",\n");
                } else {
                    first = false;
                }
                bw.write(entry.toJson());
            }
            bw.append("\n]}");
        }
        System.out.printf("Written %d entries to %s%n", entries.size(), name);
    }
}
