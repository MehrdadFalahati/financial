# Financial Application

Financial Application is a api for transfer money between any account.

## Installation

Use the Maven package manager to install Financial Application.

```bash
mvn clean package
```

## Docker

Use docker for build docker image. you can run that script in root project.

```bash
docker build -t financial/financial-docker .
```

## Run
1. run by maven
```bash
mvn spring-boot:run
```
2. run docker image
```bash
docker run -it -p8810:8810 financial/financial-docker:latest
```
## Swagger
Dashboard swagger in project is : http://localhost:8810/financial/swagger-ui/