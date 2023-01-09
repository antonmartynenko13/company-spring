# Company

Application built on Spring Boot to keep track of employees and their workload by department.

## Requirements
Java 17, Apache Maven 3.

## Technologies
- Java - 17
- Security - Bearer Authentication.
- Database - PostgreSql.
- ORM: Hibernate
- Spring Data JPA
- API Documentation: Open API
- Checkstyle/Formatter: Google formatter (https://github.com/google/google-java-format)
- Docker.
- DB migrations: Liquibase
- Spring Scheduler
- Tests - Mockito, JUnit5

## Building
Run next script in root directory
```bash
mvn clean package spring-boot:repackage
```
## Deploy
Builded .jar file could be run using
```bash
java -jar target/company-1.0.0.jar
```

## Run with Docker
After building you can easily create image and run project container with next Docker's command
```bash
docker compose up 
```

## Documentation
When the application is running, the REST API documentation can be found on the public /swagger-ui/index.html URL.

