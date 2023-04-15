package com.probendi.itparser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parses a set of pages saved from the <a href="https://www.corpusthomisticum.org">Index Thomisticus</a> and generates
 * a CSV file with the columns:
 * <ul>
 *     <li>Case</li>
 *     <li>Place</li>
 *     <li>Title</li>
 *     <li>Text</li>
 * </ul>
 * Furthermore, it generates a JSON file consisting of an array of objects:
 * {@code {"entries":[{"caseNumber":1,"placeNumber":1,"title":"Super Sent., lib. 1 q. 1 a. 1 arg. 1.","text":"..."}]}}
 */
public class App {

    private static final String USAGE = "Usage: java -jar it-parser-1.0.jar dir";

    private static final String OUT_CSV = "entries.csv";
    private static final String OUT_JSON = "entries.json";

    private static final String CASE = "Case ";
    private static final String DOT = ".";
    private static final String NBSP = "&nbsp;";
    private static final String PLACE = "Place ";
    private static final String SPAN = "</span>";
    private static final String TITLE = "<p title=\"";

    /**
     * Runs {@code it-parser}.
     *
     * @param args the command line argument
     * @throws IOException if a file cannot be read or written
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println(USAGE);
        }

        final String dir = args[0];

        List<String> files;
        try (final Stream<Path> stream = Files.list(Paths.get(dir))) {
            files = stream.map(Path::getFileName).map(Path::toString).collect(Collectors.toList());
        }
        Collections.sort(files);

        final Set<Entry> entries = new LinkedHashSet<>();
        for (final String file : files) {
            try (final Stream<String> stream = Files.lines(Path.of(dir, file))) {
                stream.forEach(line -> {
                    if (line.contains(TITLE)) {
                        // get the case number
                        line = line.substring(line.indexOf(CASE) + CASE.length());
                        final int caseNumber = Integer.parseInt(line.substring(0, line.indexOf(DOT)));

                        // get the place number
                        line = line.substring(line.indexOf(PLACE) + PLACE.length());
                        final int placeNumber = Integer.parseInt(line.substring(0, line.indexOf(DOT)));

                        // get the title
                        line = line.substring(line.indexOf(SPAN) + SPAN.length());
                        final String title = line.substring(0, line.indexOf(NBSP));

                        // get the text
                        line = line.substring(title.length() + NBSP.length());
                        final StringBuilder sb = new StringBuilder();
                        boolean add = true;
                        for (final byte b : line.getBytes(StandardCharsets.UTF_8)) {
                            if (b == '<') {
                                add = false;
                            } else if (b == '>') {
                                add = true;
                            } else if (add) {
                                sb.append((char) b);
                            }
                        }
                        final String text = sb.toString().replace(NBSP, " ");

                        entries.add(new Entry(caseNumber, placeNumber, title, text));
                    }
                });
            }
        }

        int i = 0;
        try (final FileWriter fw = new FileWriter(OUT_CSV); final BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Case\tPlace\tTitle\tText\n");
            for (final Entry entry : entries) {
                bw.write(entry.toCsv());
                ++i;
            }
        }
        System.out.printf("Written %d entries to %s%n", i, OUT_CSV);

        i = 0;
        try (final FileWriter fw = new FileWriter(OUT_JSON); final BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("{\"entries\":[");
            for (final Entry entry : entries) {
                if (i++ > 0) {
                    bw.write(",");
                }
                bw.write(entry.toJson());
            }
            bw.append("]}");
        }
        System.out.printf("Written %d entries to %s%n", i, OUT_JSON);
    }
}
