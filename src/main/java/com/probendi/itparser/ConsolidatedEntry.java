package com.probendi.itparser;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * An entry to be written to a CVS and a JSON file.
 *
 * @param placeNumber the place number
 * @param work        the work
 * @param position    the position within the work
 * @param text        the text
 *                    <p>
 *                    Copyright &copy; 2023-2024, Daniele Di Salvo
 * @author Daniele Di Salvo
 * @since 2.1
 */
public record ConsolidatedEntry(int placeNumber, String work, String position, List<String> text) {

    private static final String CSV_FORMAT = "%s\t%s\t%s\n";
    private static final String JSON_FORMAT = "{\"work\":\"%s\",\"position\":\"%s\",\"text\":\"%s\"}";

    /**
     * Creates a new entry with the given values.
     *
     * @param placeNumber the place number
     * @param work        the work
     * @param position    the position with
     * @param text        the text
     */
    public ConsolidatedEntry {
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

    /**
     * Creates a new {@code ConsolidatedEntry} from the given {@link Entry}.
     *
     * @param entry an {@link Entry}
     */
    public ConsolidatedEntry(Entry entry) {
        this(entry.placeNumber(), entry.work(), entry.position(), new LinkedList<>());
        text.add(entry.text());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final ConsolidatedEntry entry)) {
            return false;
        }
        return placeNumber == entry.placeNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeNumber);
    }

    @Override
    public String toString() {
        String shortText = "";
        if (!text.isEmpty()) {
            String t = text.get(0);
            shortText = t.length() > 32 ? t.substring(0, 32) : t;
        }
        return "Entry{" +
                "placeNumber=" + placeNumber +
                ", work='" + work + '\'' +
                ", position='" + position + '\'' +
                ", text=" + shortText +
                '}';
    }

    /**
     * Adds the given strings to the text collection.
     *
     * @param strings the strings to be added
     */
    public void addText(List<String> strings) {
        text.addAll(strings);
    }

    /**
     * Return this entry as a CSV string.
     *
     * @return this entry as a CSV string
     */
    public String toCsv() {
        return doFormat(CSV_FORMAT);
    }

    /**
     * Return this entry as a JSON string.
     *
     * @return this entry as a JSON string
     */
    public String toJson() {
        return doFormat(JSON_FORMAT);
    }

    /**
     * Returns this entry as a CSV or JSON string.
     *
     * @param format the formatting string
     * @return this entry as a CSV or JSON string
     */
    private String doFormat(String format) {
        StringBuilder sb = new StringBuilder();
        text.forEach(t -> sb.append(t).append(" "));
        return String.format(format, work, position, sb.toString().trim());
    }
}
