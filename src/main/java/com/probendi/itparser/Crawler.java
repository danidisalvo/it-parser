package com.probendi.itparser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Searches the <a href="https://www.corpusthomisticum.org/it/index.age">Index Thomisticus</a>.for all occurrences of
 * the forms of a term.
 * <p>
 * Copyright &copy; 2023-2024, Daniele Di Salvo
 *
 * @author Daniele Di Salvo
 * @since 1.0
 */
public class Crawler {

    private static final String COOKIE = "Cookie";

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    private static final int FIRST_AUTHENTIC_WORK = 0;
    private static final int LAST_AUTHENTIC_WORK = 114;

    private static final URI INDEX_THOMISTICUS = URI.create("https://www.corpusthomisticum.org/it/index.age");

    private static final int PAGE_SIZE = 10_000;

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

    private final HttpClient client = HttpClient.newHttpClient();
    private final List<String> lines;

    /**
     * Creates a new {@code Crawler} with the given parameters.
     *
     * @param lines a list of freely mixed terms, e.g., {@code ens}, lemmas, e.g., {@code #26153},
     *              and expression, e.g., {@code "id quod est"}.
     *              A term can be followed by one or more forms, e.g., {@code 78} (ens) and {@code 79} (entis)
     * @throws IllegalArgumentException if term is {@code null} or empty, or if forms is {@code null}
     */
    public Crawler(List<String> lines) {
        this.lines = lines;
    }

    /**
     * Searches the {@code Index Thomisticus} for all occurrences of the given {@code term}'s {@code forms}.
     *
     * @return a list of {@link ConsolidatedEntry} objects
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    public List<ConsolidatedEntry> crawl() throws IOException, InterruptedException {
        Set<Entry> entries = new TreeSet<>();

        for (String line : lines) {
            String sessionId = sendNewSearchRequest();

            String term;
            if (!line.startsWith("#") && !line.startsWith("\"")) {
                String[] strings = line.split(" ");
                term = strings[0];
                sendTermRequest(sessionId, term);
                sendFormsRequest(sessionId, strings);
            } else {
                term = line;
            }

            sendWorksRequest(sessionId, term);
            entries.addAll(sendConcordancesRequest(sessionId, term));
        }

        List<ConsolidatedEntry> consolidatedEntries = new ArrayList<>();
        entries.forEach(e -> {
            ConsolidatedEntry entry = new ConsolidatedEntry(e);
            int size = consolidatedEntries.size() - 1;
            if (size < 0 || !consolidatedEntries.get(size).equals(entry)) {
                consolidatedEntries.add(entry);
            } else {
                consolidatedEntries.get(size).addText(entry.text());
            }
        });
        return consolidatedEntries;
    }

    /**
     * Sends the 'new search' request.
     *
     * @return the new session's ID
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    protected String sendNewSearchRequest() throws IOException, InterruptedException {
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
     * @param term      the term
     * @throws IllegalArgumentException if sessionId is {@code null}
     * @throws IOException              if an I/O error occurs
     * @throws InterruptedException     if the thread is interrupted
     */
    protected void sendTermRequest(String sessionId, String term) throws IOException, InterruptedException {
        if (sessionId == null) {
            throw new IllegalArgumentException("sessionId cannot be null");
        }
        String reqBody = String.format("text=%s&Form.option.terms=terms", term);
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
     * @param strings   the term and its forms, if any
     * @throws IllegalArgumentException if sessionId is {@code null}
     * @throws IOException              if an I/O error occurs
     * @throws InterruptedException     if the thread is interrupted
     */
    protected void sendFormsRequest(String sessionId, String[] strings) throws IOException, InterruptedException {
        if (sessionId == null) {
            throw new IllegalArgumentException("sessionId cannot be null");
        }
        if (strings.length == 1) {
            // the term has no forms
            return;
        }
        StringBuilder reqBody = new StringBuilder(String.format("text=%s&Form.option.works=works", strings[0]));
        String format = "&terms%%5B0%%5D.listedLemmata%%5B0%%5D.listedFormae%%5B%s%%5D.selected=on";
        for (int i = 1; i < strings.length; i++) {
            reqBody.append(String.format(format, strings[i]));
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
     * @param term      the term
     * @throws IllegalArgumentException if sessionId is {@code null}
     * @throws IOException              if an I/O error occurs
     * @throws InterruptedException     if the thread is interrupted
     */
    protected void sendWorksRequest(String sessionId, String term) throws IOException, InterruptedException {
        if (sessionId == null) {
            throw new IllegalArgumentException("sessionId cannot be null");
        }
        StringBuilder reqBody = new StringBuilder(String.format("text=%s&Form.option.options=options", term));
        String format = "&listedWorks%%5B%d%%5D.selected=on";
        for (int i = FIRST_AUTHENTIC_WORK; i <= LAST_AUTHENTIC_WORK; i++) {
            reqBody.append(String.format(format, i));
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
     * @param term      the term
     * @return a set of parsed {@link Entry} objects
     * @throws IllegalArgumentException if sessionId is {@code null}
     * @throws IOException              if an I/O error occurs
     * @throws InterruptedException     if the thread is interrupted
     */
    protected Set<Entry> sendConcordancesRequest(String sessionId, String term) throws IOException, InterruptedException {
        if (sessionId == null) {
            throw new IllegalArgumentException("sessionId cannot be null");
        }
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
        System.out.println(body.substring(m, n) + " cases for " + term);

        Set<Entry> entries = new TreeSet<>();
        Pattern.compile("<p title=.*")
                .matcher(body)
                .results()
                .map(MatchResult::group).
                forEach(line -> entries.add(Entry.parseEntry(line)));
        return entries;
    }
}
