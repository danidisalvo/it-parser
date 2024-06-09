package com.probendi.itparser;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * An entry parsed from the <a href="https://www.corpusthomisticum.org/it/index.age">Index Thomisticus</a>.
 *
 * @param caseNumber  the case number
 * @param placeNumber the place number
 * @param work        the work
 * @param position    the position within the work
 * @param text        the text
 *
 * Copyright &copy; 2023-2024, Daniele Di Salvo
 *
 * @author Daniele Di Salvo
 * @since 1.0
 */
public record Entry(int caseNumber,
                    int placeNumber,
                    String work,
                    String position,
                    String text) implements Comparable<Entry> {

    private static final String CASE = "Case ";
    private static final String COMMA = ",";
    private static final String DOT = ".";
    private static final String NBSP = "&nbsp;";
    private static final String PLACE = "Place ";
    private static final String SPAN = "</span>";

    /**
     * Creates a new entry with the given values.
     *
     * @param caseNumber  the case number
     * @param placeNumber the place number
     * @param work        the work
     * @param position    the position within the work
     * @param text        the text
     */
    public Entry {
        if (caseNumber <= 0) {
            throw new IllegalArgumentException("caseNumber must be strictly positive");
        }
        if (placeNumber <= 0) {
            throw new IllegalArgumentException("placeNumber must be strictly positive");
        }
        if (work == null) {
            throw new IllegalArgumentException("work cannot be null");
        }
        if (position == null) {
            throw new IllegalArgumentException("position cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Entry entry)) {
            return false;
        }
        return placeNumber == entry.placeNumber &&
                work.equals(entry.work) &&
                position.equals(entry.position) &&
                text.equals(entry.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeNumber, work, position, text);
    }

    @Override
    public String toString() {
        return "Entry{" +
                "caseNumber=" + caseNumber +
                ", placeNumber=" + placeNumber +
                ", work='" + work + '\'' +
                ", position='" + position + '\'' +
                ", text=" + (text.length() > 32 ? text.substring(0, 32) : text) +
                '}';
    }

    @Override
    public int compareTo(final Entry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("entry cannot be null");
        }
        return Integer.compare(caseNumber, entry.caseNumber);
    }

    /**
     * Parses the given line.
     *
     * @param line the line to be parsed
     * @return a new {@link Entry}
     * @throws IllegalArgumentException if line is {@code null}
     */
    public static Entry parseEntry(String line) {
        if (line == null) {
            throw new IllegalArgumentException("line cannot be null");
        }
        // get the case number
        line = line.substring(line.indexOf(CASE) + CASE.length());
        final int caseNumber = Integer.parseInt(line.substring(0, line.indexOf(DOT)));

        // get the place number
        line = line.substring(line.indexOf(PLACE) + PLACE.length());
        final int placeNumber = Integer.parseInt(line.substring(0, line.indexOf(DOT)));

        // get the work and the position within it
        line = line.substring(line.indexOf(SPAN) + SPAN.length());
        final String position = line.substring(0, line.indexOf(NBSP));
        int n = position.indexOf(COMMA);
        String work = n == -1 ? position : position.substring(0, n);

        // get the text
        line = line.substring(position.length() + NBSP.length());
        final StringBuilder sb = new StringBuilder();
        boolean add = true;
        for (byte b : line.getBytes(StandardCharsets.UTF_8)) {
            if (b == '<') {
                add = false;
            } else if (b == '>') {
                add = true;
            } else if (add) {
                sb.append((char) b);
            }
        }
        final String text = sb.toString().replace(NBSP, " ");

        return new Entry(caseNumber, placeNumber, work, position, text);
    }
}
