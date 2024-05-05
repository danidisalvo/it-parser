package com.probendi.itparser;

import java.io.IOException;
import java.util.Set;

/**
 * Writes entries to a file.
 * <p>
 * Copyright &copy; 2023-2024, Daniele Di Salvo
 *
 * @author Daniele Di Salvo
 * @since 2.0
 */
public interface Writer {

    /**
     * Writes the given entries to a file.
     *
     * @param file    the file's name
     * @param entries the entries to be written
     * @throws IllegalArgumentException if file is {@code null} or empty, or if entries is {@code null}
     * @throws IOException              if an I/O error occurs
     */
    void write(String file, Set<Entry> entries) throws IOException;

    /**
     * Validates the given file and entries.
     *
     * @param file the file's name
     * @param entries the entries
     * @return the trimmed file's name
     * @throws IllegalArgumentException if file is {@code null} or empty, or if entries is {@code null}
     */
    default String validate(String file, Set<Entry> entries) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("file cannot be blank");
        }
        if (entries == null) {
            throw new IllegalArgumentException("entries cannot be null");
        }
        return file.trim();
    }
}
