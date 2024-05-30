# it-parser
Queries the <a href="https://www.corpusthomisticum.org/it/index.age">Index Thomisticus</a>, 
parses the query's result and generates CSV and JSON files for further analysis.

The CSV file contains the following columns:
- Case
- Place
- Work
- Position
- Text

The JSON file jas the following structure:
```
{
  "entries":[
    {
      "caseNumber":1,
      "placeNumber":1,
      "work":"Super Sent.",
      "position":"Super Sent., lib. 1 q. 1 a. 1 arg. 1.",
      "text":"..."
    }
  ]
}
```

## How to Build and Run it-parser

```
mvn clean package

java -jar target/it-parserl-2.0.jar term [form1] [form2] ...
```

where `term` is the term to be searched, and the optional `form1`, `form2` are
the term's forms to be searched. 

For example:

```
java -jar target/it-parser.jar ens 78 79 80 81 82 83 84 85 86 87
```

searches for `ens`, `entis`, `enti`, `entem`, `ente`, `enter`, `entes`, `entia`, `entium`, and `entibus`.




