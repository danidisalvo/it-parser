# it-parser
Parses a set of pages saved from the <a href="https://www.corpusthomisticum.org">Index Thomisticus</a> and generates 
CSV and JSON files for further analysis.

The CSV file contains the following columns:
- Case
- Place
- Title
- Text

The JSON file jas the following structure:
```
{
  "entries":[
    {
      "caseNumber":1,
      "placeNumber":1,
      "title":"Super Sent., lib. 1 q. 1 a. 1 arg. 1.",
      "text":"..."
    }
  ]
}
```

## How to Build and Run it-parser

```
mvn clean package

java -jar target/it-parserl-1.0.jar dir
```

where `dir` contains the saved html pages.
