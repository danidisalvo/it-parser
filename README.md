# it-parser
Queries the <a href="https://www.corpusthomisticum.org/it/index.age">Index Thomisticus</a>, 
parses the query's result and generates CSV and JSON files for further analysis.

The CSV file contains the following columns:
- Work
- Position
- Text

The JSON file jas the following structure:
```
{
  "entries":[
    {
      "work":"Super Sent.",
      "position":"Super Sent., lib. 1 q. 1 a. 1 arg. 1.",
      "text":"..."
    }
  ]
}
```

## How to Build and Run it-parser

```
mvn clean install

java -jar target/it-parser-3.0.1.jar input_file
```

where `input_file` is the file containing the terms, lemmas and expressions to be searched. 

For example:

```
java -jar target/it-parser-3.0.1.jar input.txt
```

searches for:
- `ens`, `entis`, `enti`, `entem`, `ente`, `enter`, `entes`, `entia`, `entium`, and `entibus`;
- the lemma `#26153`, i.e., the verb `dīvĭdo, dīvĭdis, divisi, divisum, dīvĭdĕre`;
- the expressions `id quod est`, `ea quae sunt`, and `omnia sunt`.
