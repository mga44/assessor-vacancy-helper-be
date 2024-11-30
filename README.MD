# Assessor vacancy helper (BE)

## Introduction

Java program for fetching list of vacancies in polish courts (presented in PDF format) and producing a JSON output with following properties:
```json
[
   {
      "courtName": "string",
      "courtDepartment": "string",
      "vacancy": 123,
      "appelation": "string",
      "latitude": 123.12,
      "longitude": 123.12,
      "cityPopulation": "string",
      "distanceFromWarsaw": "string"
   },
   {
      ...
   }
]
```
JSON file can be passed to other project [assessor-vacancy-helper-fe](https://github.com/mga44/assessor-vacancy-helper-fe), which handles data display.

## Features

- parse PDF with vacancies
- fetch latitude and longitude from [nominatim.openstreetmap.org](https://nominatim.openstreetmap.org)
- fetch city population from GUS register (CSV file) 
- compute distance from Warsaw
- generate cache for external materials & allow for manual input manipulation

Steps disablement serves as a way for easily fixing data by hand.

## Installation

1. **Prerequisites**:
   * Java 17
   * Gradle 8.2
2. **Clone the repo**:
```bash
   git clone https://github.com/mga44/assessor-vacancy-helper-be.git
   cd assessor-vacancy-helper-be
```

## Usage
### First run
Start the application:

```bash
gradle run --args='-f=src/main/resources/zarzadzenie_ms_z_26.04.24_-_wykaz_wolnych_stanowisk_asesorskich.pdf -s=PARSE,SANITIZE,MAP,GEO_COORDINATE,ENRICH'
```

#### Note following directories & files created: 
- `cache\` - used for storing information from external services 
- `result\` - directory or r+w operations for each step. If step is disabled, next one will load result file from the past. 
- `result\coordinates_XXXX.json` - result file data required
- `out\` - directory used for logging data from intermediate steps, most of the time duplicates data in `result\*`
- `out\app.log` - logs from application

## Configuration
```
-f,--file <arg>    Specify the PDF file to process. Used only in parsing
step.
-h,--help          Show help
-s,--steps <arg>   Steps to be executed, separated by comma. Defaults to PARSE,SANITIZE,MAP,GEO_COORDINATE,ENRICH
```

## Contributing
If you see an area for improvement open a PR or reach out to me to propose changes.

## TODO
* [x] [BE] get population, dist. to warsaw
* [x] [BE] add ID field
* [ ] ~~[FE] label translation~~
* [x] [BE] better getting coordinates
* [ ] ~~[FE] add filtering - city, distance, department~~ - this is easily achieved by changing json files, not priority.
* [ ] [BE] change the flow - pass files / filenames instead of contents to allow better flow
* [x] [BE] cache coordinates to courts
* [x] [FE] allow multiple files
* [x] [FE] rebind popups
* [ ] [BE] price 4 m2
* [ ] [BE] rework step logic - makes more sense to pass first step to be executed
