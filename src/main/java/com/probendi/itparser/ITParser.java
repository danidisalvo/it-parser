package com.probendi.itparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

/**
 * Queries the <a href="https://www.corpusthomisticum.org/it/index.age">Index Thomisticus</a>, parses the query's
 * result and generates a CSV file with the columns:
 * <ul>
 *     <li>Case</li>
 *     <li>Place</li>
 *     <li>Work</li>
 *     <li>Position</li>
 *     <li>Text</li>
 * </ul>
 * Furthermore, it generates a JSON file consisting of an array of objects:
 * {@code {"entries":[{"caseNumber":1,"placeNumber":1,"position":"Super Sent.",
 * "position":"Super Sent., lib. 1 q. 1 a. 1 arg. 1.","text":"..."}]}}
 * <p>
 * Copyright &copy; 2023-2024, Daniele Di Salvo
 *
 * @author Daniele Di Salvo
 * @since 1.0
 */
public class ITParser {

    private static final String USAGE = "Usage: java -jar it-parser-2.0.jar term [form1] [form2] ...";

    private static final String CSV_FILE = "entries.csv";
    private static final String JSON_FILE = "entries.json";

    /**
     * Runs {@code it-parser}.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(-1);
        }

        String term = args[0];
        String[] forms = args.length == 1 ?
                new String[0] :
                Arrays.stream(args, 1, args.length - 1).toArray(String[]::new);

        if (forms.length == 0) {
            forms = getWellKnownForms(term);
        }

        Crawler crawler = new Crawler(term, forms);
        try {
            Set<Entry> entries = crawler.crawl();
            new CsvWriter().write(CSV_FILE, entries);
            new JsonWriter().write(JSON_FILE, entries);

            System.out.printf("execution time: %d s\n", (System.currentTimeMillis() - start) / 1000);
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Returns the well-known forms of a term from the properties file.
     *
     * @param term the term
     * @return the term's well-known forms
     * @throws IllegalArgumentException if term is {@code null}
     */
    static String[] getWellKnownForms(String term) {
        ClassLoader classLoader = ITParser.class.getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("forms.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            String property = properties.getProperty(term);
            if (property != null) {
                return property.split(",");
            }
        } catch (IOException e) {
            System.out.println("failed to read the properties file");
        }
        return new String[0];
    }
}
