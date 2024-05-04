package com.probendi.itparser;

import java.util.Objects;

/**
 * An entry to be written to a CVS and a JSON file.
 *
 * @param caseNumber  the case number
 * @param placeNumber the place number
 * @param work        the work
 * @param position    the position with
 * @param text        the text
 */
public record Entry(int caseNumber,
                    int placeNumber,
                    String work,
                    String position,
                    String text) implements Comparable<Entry> {

    private static final String CSV_FORMAT = "%d\t%d\t%s\t%s\t%s\n";
    private static final String JSON_FORMAT =
            "{\"caseNumber\":%d,\"placeNumber\":%d,\"work\":\"%s\",\"position\":\"%s\"\"text\":\"%s\"}";

    /**
     * Creates a new entry with the given values.
     *
     * @param caseNumber  the case number
     * @param placeNumber the place number
     * @param work        the work
     * @param position    the position with
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
        return Objects.hash(/*caseNumber,*/ placeNumber, work, position, text);
    }

    @Override
    public String toString() {
        return "Entry{" +
                "caseNumber=" + caseNumber +
                ", placeNumber=" + placeNumber +
                ", work=" + work +
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
     * Return this entry as a CSV string.
     *
     * @return this entry as a CSV string
     */
    public String toCsv() {
        return String.format(CSV_FORMAT, caseNumber, placeNumber, work, position, text);
    }

    /**
     * Return this entry as a JSON string.
     *
     * @return this entry as a JSON string
     */
    public String toJson() {
        return String.format(JSON_FORMAT, caseNumber, placeNumber, work, position, text);
    }
}
