package com.probendi.itparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Queries the <a href="https://www.corpusthomisticum.org/it/index.age">Index Thomisticus</a>, parses the query's
 * result and generates a CSV file with the columns:
 * <ul>
 *     <li>Work</li>
 *     <li>Position</li>
 *     <li>Text</li>
 * </ul>
 * Furthermore, it generates a JSON file consisting of an array of objects:
 * {@code {"entries":[{"position":"Super Sent.","position":"Super Sent., lib. 1 q. 1 a. 1 arg. 1.","text":"..."}]}}
 * <p>
 * Copyright &copy; 2023-2024, Daniele Di Salvo
 *
 * @author Daniele Di Salvo
 * @since 1.0
 */
public class ITParser {

    private static final String USAGE = "Usage: java -jar it-parser-3.0.jar input";

    private static final String CSV_FILE = "entries.csv";
    private static final String JSON_FILE = "entries.json";

    /**
     * Runs {@code it-parser}.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(-1);
        }

        Path path = Paths.get(args[0]);
        List<String> terms = new LinkedList<>();
        try {
            terms.addAll(Files.readAllLines(path));
        } catch (IOException e) {
            System.err.println("failed to read input file " + args[0]);
            System.exit(-1);
        }

        Crawler crawler = new Crawler(terms);
        try {
            List<ConsolidatedEntry> entries = crawler.crawl();
            new CsvWriter().write(CSV_FILE, entries);
            new JsonWriter().write(JSON_FILE, entries);

            System.out.printf("execution time: %d s\n", (System.currentTimeMillis() - start) / 1000);
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
