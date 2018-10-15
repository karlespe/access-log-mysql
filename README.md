# Access Log MySQL Analysis Tool

Spring Boot application that imports a delimited file into MySQL and analyzes requests based on time periods and thresholds
using JPA.

## Getting Started

Build the application jar using Spring Boot ...

```
gradle bootJar
```

Run the jar directly ...

```
java -jar parser.jar --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=200 --accesslog=path/to/pipe/delimited/log
```

## Author

* **Karl Espe** - *Initial work* - (https://github.com/karlespe)

