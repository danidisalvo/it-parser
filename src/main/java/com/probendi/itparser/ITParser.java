package com.probendi.itparser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

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
 */
public class ITParser {

    private static final String USAGE = "Usage: java -jar it-parser-2.0.jar term [form1] [form2] ...";

    private static final String OUT_CSV = "entries.csv";
    private static final String OUT_JSON = "entries.json";

    private static final String COOKIE = "Cookie";

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    private static final URI INDEX_THOMISTICUS = URI.create("https://www.corpusthomisticum.org/it/index.age");

    private static final int PAGE_SIZE = 10_000;

    private static final int FIRST_AUTHENTIC_WORK = 0;
    private static final int LAST_AUTHENTIC_WORK = 114;

    private static final String CASE = "Case ";
    private static final String DOT = ".";
    private static final String NBSP = "&nbsp;";
    private static final String PLACE = "Place ";
    private static final String SPAN = "</span>";
    private static final String TITLE = "<p title=.*";

    private static final String FORM_DATA_TERM = "text=%s&Form.option.terms=terms";

    private static final String FORM_DATA_FORMS = "text=%s&Form.option.works=works";
    private static final String FORM_DATA_FORM =
            "&terms%%5B0%%5D.listedLemmata%%5B0%%5D.listedFormae%%5B%d%%5D.selected=on";

    private static final String FORM_DATA_WORKS = "text=%s&Form.option.options=options";
    private static final String FORM_DATA_WORK = "&listedWorks%%5B%d%%5D.selected=on";

    private static final String FORM_DATA_CONCORDANCES = "&exhaustive=false" +
            "&asyndetonAll=true" +
            "&ordered=false" +
            "&minWordsBetweenTermsNoOrder=0" +
            "&maxWordsBetweenTermsNoOrder=0" +
            "&minWordsBetweenTermsInOrder=0" +
            "&maxWordsBetweenTermsInOrder=0" +
            "&allTextualUnits=true" +
            "&textualUnits.booleanOptions%5B0%5D.selected=on" +
            "&textualUnits.booleanOptions%5B1%5D.selected=on" +
            "&textualUnits.booleanOptions%5B2%5D.selected=on" +
            "&textualUnits.booleanOptions%5B3%5D.selected=on" +
            "&textualUnits.booleanOptions%5B4%5D.selected=on" +
            "&textualUnits.booleanOptions%5B5%5D.selected=on" +
            "&textualUnits.booleanOptions%5B6%5D.selected=on" +
            "&textualUnits.booleanOptions%5B7%5D.selected=on" +
            "&textualUnits.booleanOptions%5B8%5D.selected=on" +
            "&textualUnits.booleanOptions%5B9%5D.selected=on" +
            "&divideByPeriods=false" +
            "&authorshipAll=true" +
            "&divideByAuthorship=false" +
            "&authorship.booleanOptions%5B0%5D.selected=on" +
            "&authorship.booleanOptions%5B1%5D.selected=on" +
            "&authorship.booleanOptions%5B2%5D.selected=on" +
            "&authorship.booleanOptions%5B3%5D.selected=on" +
            "&authorship.booleanOptions%5B4%5D.selected=on" +
            "&authorship.booleanOptions%5B5%5D.selected=on" +
            "&results.pageSize=" + PAGE_SIZE +
            "&results.presentation=1" +
            "&results.additionalPeriods=0" +
            "&results.additionalLines=2" +
            "&results.includePosition=false" +
            "&results.additionalRecords=2" +
            "&matchOptions.options%5B0%5D.homographsOnly=true" +
            "&matchOptions.options%5B0%5D.booleanOptions%5B0%5D.selected=on" +
            "&matchOptions.options%5B0%5D.booleanOptions%5B1%5D.selected=on" +
            "&matchOptions.options%5B0%5D.booleanOptions%5B2%5D.selected=on" +
            "&matchOptions.options%5B0%5D.booleanOptions%5B3%5D.selected=on" +
            "&matchOptions.options%5B0%5D.booleanOptions%5B4%5D.selected=on" +
            "&matchOptions.options%5B0%5D.booleanOptions%5B5%5D.selected=on" +
            "&matchOptions.options%5B0%5D.booleanOptions%5B6%5D.selected=on" +
            "&matchOptions.options%5B0%5D.booleanOptions%5B7%5D.selected=on" +
            "&matchOptions.options%5B0%5D.booleanOptions%5B8%5D.selected=on" +
            "&matchOptions.options%5B1%5D.homographsOnly=true" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B0%5D.selected=on" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B1%5D.selected=on" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B2%5D.selected=on" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B3%5D.selected=on" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B4%5D.selected=on" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B5%5D.selected=on" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B6%5D.selected=on" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B7%5D.selected=on" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B8%5D.selected=on" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B9%5D.selected=on" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B10%5D.selected=on" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B11%5D.selected=on" +
            "&matchOptions.options%5B1%5D.booleanOptions%5B12%5D.selected=on" +
            "&matchOptions.options%5B2%5D.homographsOnly=true" +
            "&matchOptions.options%5B2%5D.booleanOptions%5B0%5D.selected=on" +
            "&matchOptions.options%5B2%5D.booleanOptions%5B1%5D.selected=on" +
            "&matchOptions.options%5B2%5D.booleanOptions%5B2%5D.selected=on" +
            "&matchOptions.options%5B2%5D.booleanOptions%5B3%5D.selected=on" +
            "&matchOptions.options%5B2%5D.booleanOptions%5B4%5D.selected=on" +
            "&matchOptions.options%5B2%5D.booleanOptions%5B5%5D.selected=on" +
            "&matchOptions.options%5B2%5D.booleanOptions%5B6%5D.selected=on" +
            "&matchOptions.options%5B2%5D.booleanOptions%5B7%5D.selected=on" +
            "&matchOptions.options%5B2%5D.booleanOptions%5B8%5D.selected=on" +
            "&matchOptions.options%5B2%5D.booleanOptions%5B9%5D.selected=on" +
            "&matchOptions.options%5B2%5D.booleanOptions%5B10%5D.selected=on" +
            "&matchOptions.options%5B3%5D.homographsOnly=true" +
            "&matchOptions.options%5B3%5D.booleanOptions%5B0%5D.selected=on" +
            "&matchOptions.options%5B3%5D.booleanOptions%5B1%5D.selected=on" +
            "&matchOptions.options%5B3%5D.booleanOptions%5B2%5D.selected=on" +
            "&matchOptions.options%5B3%5D.booleanOptions%5B3%5D.selected=on" +
            "&matchOptions.options%5B4%5D.homographsOnly=true" +
            "&matchOptions.options%5B4%5D.booleanOptions%5B0%5D.selected=on" +
            "&matchOptions.options%5B4%5D.booleanOptions%5B1%5D.selected=on" +
            "&matchOptions.options%5B4%5D.booleanOptions%5B2%5D.selected=on" +
            "&matchOptions.options%5B4%5D.booleanOptions%5B3%5D.selected=on" +
            "&matchOptions.options%5B4%5D.booleanOptions%5B4%5D.selected=on" +
            "&matchOptions.options%5B4%5D.booleanOptions%5B5%5D.selected=on" +
            "&matchOptions.options%5B4%5D.booleanOptions%5B6%5D.selected=on" +
            "&matchOptions.options%5B4%5D.booleanOptions%5B7%5D.selected=on" +
            "&matchOptions.options%5B4%5D.booleanOptions%5B8%5D.selected=on" +
            "&divisionNumber=2" +
            "&Form.option.concordances=concordances";

    private static final List<Integer> ENS_FORMS = List.of(78, 79, 80, 81, 82, 83, 84, 85, 86, 87);

    private final HttpClient client = HttpClient.newHttpClient();
    private final String term;
    private final List<Integer> forms;

    /**
     * Runs {@code it-parser}.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(-1);
        }

        String term = args[0];
        List<Integer> forms = new LinkedList<>();
        if (args.length == 1 && "ens".equalsIgnoreCase(term)) {
            forms = ENS_FORMS;
        }
        for (int i = 0; i < args.length - 1; i++) {
            forms.add(Integer.parseInt(args[i]));
        }

        ITParser parser = new ITParser(term, forms);
        try {
            Set<Entry> entries = parser.parse();
            parser.writeAsCsv(entries);
            parser.writeAsJson(entries);
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Creates a new parser which find all occurrence of the given term and forms.
     *
     * @param term  the term, e.g., {@code ens}
     * @param forms the lemma's forms, e.g., {@code 78} (ens) and {@code 79} (entis)
     */
    public ITParser(String term, List<Integer> forms) {
        this.term = term;
        this.forms = forms;
    }

    /**
     * Parses the {@code Index Thomisticus} for all occurrences of the given term in all its forms.
     *
     * @return a set of parsed {@link Entry} objects
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    public Set<Entry> parse() throws IOException, InterruptedException {
        String sessionId = sendNewSearchRequest();
        sendTermRequest(sessionId);
        sendFormsRequest(sessionId);
        sendWorksRequest(sessionId);
        return sendConcordancesRequest(sessionId);
    }

    /**
     * Sends the 'new search' request.
     *
     * @return the new session's ID
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    private String sendNewSearchRequest() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(INDEX_THOMISTICUS)
                .header(CONTENT_TYPE, CONTENT_TYPE_FORM)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("the 'new search' request returned " + response.statusCode());
            System.exit(-1);
        }
        String header = response.headers().firstValue("Set-Cookie").orElse("");
        return header.substring(0, header.indexOf(";"));
    }

    /**
     * Sends the 'term' request.
     *
     * @param sessionId the session's ID
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    private void sendTermRequest(String sessionId) throws IOException, InterruptedException {
        String reqBody = String.format(FORM_DATA_TERM, term);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(INDEX_THOMISTICUS)
                .header(CONTENT_TYPE, CONTENT_TYPE_FORM)
                .header(COOKIE, sessionId)
                .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("the 'ens' request returned " + response.statusCode());
            System.exit(-1);
        }
    }

    /**
     * Sends the 'forms' request.
     *
     * @param sessionId the session's ID
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    private void sendFormsRequest(String sessionId) throws IOException, InterruptedException {
        StringBuilder reqBody = new StringBuilder(String.format(FORM_DATA_FORMS, term));
        for (int form : forms) {
            reqBody.append(String.format(FORM_DATA_FORM, form));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(INDEX_THOMISTICUS)
                .header(CONTENT_TYPE, CONTENT_TYPE_FORM)
                .header(COOKIE, sessionId)
                .POST(HttpRequest.BodyPublishers.ofString(reqBody.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("the 'terms' request returned " + response.statusCode());
            System.exit(-1);
        }
    }

    /**
     * Sends the 'works' request.
     *
     * @param sessionId the session's ID
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    private void sendWorksRequest(String sessionId) throws IOException, InterruptedException {
        StringBuilder reqBody = new StringBuilder(String.format(FORM_DATA_WORKS, term));
        for (int i = FIRST_AUTHENTIC_WORK; i <= LAST_AUTHENTIC_WORK; i++) {
            reqBody.append(String.format(FORM_DATA_WORK, i));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(INDEX_THOMISTICUS)
                .header(CONTENT_TYPE, CONTENT_TYPE_FORM)
                .header(COOKIE, sessionId)
                .POST(HttpRequest.BodyPublishers.ofString(reqBody.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("the 'works' request returned " + response.statusCode());
            System.exit(-1);
        }
    }

    /**
     * Sends the 'concordances' request.
     *
     * @param sessionId the session's ID
     * @return a set of parsed {@link Entry} objects
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    private Set<Entry> sendConcordancesRequest(String sessionId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(INDEX_THOMISTICUS)
                .header(CONTENT_TYPE, CONTENT_TYPE_FORM)
                .header(COOKIE, sessionId)
                .POST(HttpRequest.BodyPublishers.ofString("text=" + term + FORM_DATA_CONCORDANCES))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("the 'concordances' request returned " + response.statusCode());
            System.exit(-1);
        }

        String body = response.body();
        int m = body.indexOf("Found");
        int n = body.indexOf(" cases in ");
        System.out.println(body.substring(m, n) + " cases");

        final Set<Entry> entries = new LinkedHashSet<>();
        Pattern.compile(TITLE)
                .matcher(body)
                .results()
                .map(MatchResult::group).
                forEach(match -> entries.add(parseEntry(match)));
        return entries;
    }

    /**
     * Parses the given line.
     *
     * @param line the line to be parsed
     * @return a new {@link Entry}
     */
    private Entry parseEntry(String line) {
        // get the case number
        line = line.substring(line.indexOf(CASE) + CASE.length());
        final int caseNumber = Integer.parseInt(line.substring(0, line.indexOf(DOT)));

        // get the place number
        line = line.substring(line.indexOf(PLACE) + PLACE.length());
        final int placeNumber = Integer.parseInt(line.substring(0, line.indexOf(DOT)));

        // get the work and the position within it
        line = line.substring(line.indexOf(SPAN) + SPAN.length());
        final String position = line.substring(0, line.indexOf(NBSP));
        int n = position.indexOf(",");
        String work = n == - 1 ? position : position.substring(0, n);

        // get the text
        line = line.substring(position.length() + NBSP.length());
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

        return new Entry(caseNumber, placeNumber, work, position, text);
    }

    /**
     * Writes the given entries into a CSV file.
     *
     * @param entries the entries to be saved
     * @throws IOException if an I/O error occurs
     */
    private void writeAsCsv(Set<Entry> entries) throws IOException {
        try (final FileWriter fw = new FileWriter(OUT_CSV); final BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Case\tPlace\tTitle\tText\n");
            for (final Entry entry : entries) {
                bw.write(entry.toCsv());
            }
        }
        System.out.printf("Written %d entries to %s%n", entries.size(), OUT_CSV);

    }

    /**
     * Writes the given entries into a JSON file.
     *
     * @param entries the entries to be saved
     * @throws IOException if an I/O error occurs
     */
    private void writeAsJson(Set<Entry> entries) throws IOException {
        boolean first = true;
        try (final FileWriter fw = new FileWriter(OUT_JSON); final BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("{\"entries\":[");
            for (final Entry entry : entries) {
                if (!first) {
                    bw.write(",");
                } else {
                    first = false;
                }
                bw.write(entry.toJson());
            }
            bw.append("]}");
        }
        System.out.printf("Written %d entries to %s%n", entries.size(), OUT_JSON);
    }
}
