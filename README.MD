# Assessor vacancy helper (BE)

## Introduction

Java program for fetching list of vacancies in polish courts (presented in PDF format) and producing a JSON output with following properties:
```json
[
  {
    "courtName": "string",
    "courtDepartment": "string",
    "vacancy": number,
    "appelation": "string",
    "latitude": number,
    "longitude": number,
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
- fetch latitude and longitude from external API
- fetch city population from GUS register (CSV file) 
- compute distance from Warsaw

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
Start the application:

```bash
gradle run --args='-f=src/main/resources/zarzadzenie_ms_z_26.04.24_-_wykaz_wolnych_stanowisk_asesorskich.pdf -s=PARSE,SANITIZE,MAP,GEO_COORDINATE,ENRICH'
```

## Basic Functionality:
Describe the core functions and how to use them, such as selecting JSON files for the map.

## Configuration
```
-f,--file <arg>    Specify the PDF file to process. Used only in parsing
step.
-h,--help          Show help
-s,--steps <arg>   Steps to be executed, separated by comma. Defaults to
PARSE,SANITIZE,MAP,GEO_COORDINATE,ENRICH
```



## Contributing
If you see an area for improvement open a PR or reach out to me to propose changes.

# TODO
* [x] [BE] get population, dist. to warsaw
* [x] [BE] add ID field
* [FE] label translation
* [x] [BE] better getting coordinates
* [FE] add filtering - city, distance, department
* [BE] change the flow - pass files / filenames instead of contents to allow better flow
* [x] [BE] cache coordinates to courts
* [x] [FE] allow multiple files
* [x] [FE] rebind popups
