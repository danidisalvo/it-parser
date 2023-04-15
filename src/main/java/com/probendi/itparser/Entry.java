package com.probendi.itparser;

import java.util.Objects;

/**
 * An entry to be written to a CVS and a JSON file.
 *
 * @param caseNumber  the case number
 * @param placeNumber the place number
 * @param title       the title
 * @param text        the text
 */
public record Entry(Integer caseNumber, Integer placeNumber, String title, String text) implements Comparable<Entry> {

    private static final String CSV_FORMAT = "%d\t%d\t%s\t%s\n";
    private static final String JSON_FORMAT = "{\"caseNumber\":%d,\"placeNumber\":%d,\"title\":\"%s\",\"text\":\"%s\"}";

    /**
     * Creates a new entry with the given values.
     *
     * @param caseNumber  the case number
     * @param placeNumber the place number
     * @param title       the title
     * @param text        the text
     */
    public Entry {
        if (caseNumber == null || caseNumber <= 0) {
            throw new IllegalArgumentException("caseNumber must be strictly positive");
        }
        if (placeNumber == null || placeNumber <= 0) {
            throw new IllegalArgumentException("placeNumber must be strictly positive");
        }
        if (title == null) {
            throw new IllegalArgumentException("title cannot be null");
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
        return placeNumber.equals(entry.placeNumber) &&
                title.equals(entry.title) &&
                text.equals(entry.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(/*caseNumber,*/ placeNumber, title, text);
    }

    @Override
    public String toString() {
        return "Entry{" +
                "caseNumber=" + caseNumber +
                ", placeNumber=" + placeNumber +
                ", title='" + title + '\'' +
                ", text=" + (text.length() > 32 ? text.substring(0, 32) : text) +
                '}';
    }

    @Override
    public int compareTo(final Entry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("entry cannot be null");
        }
        return caseNumber.compareTo(entry.caseNumber);
    }

    /**
     * Return this entry as a CSV string.
     *
     * @return this entry as a CSV string
     */
    public String toCsv() {
        return String.format(CSV_FORMAT, caseNumber, placeNumber, title, text);
    }

    /**
     * Return this entry as a JSON string.
     *
     * @return this entry as a JSON string
     */
    public String toJson() {
        return String.format(JSON_FORMAT, caseNumber, placeNumber, title, text);
    }
}
