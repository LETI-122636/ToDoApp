# App README

- [ ] TODO Replace or update this README with instructions relevant to your application

# Pipeline de Build e Upload de JAR

Este projeto inclui uma pipeline CI/CD configurada através do GitHub Actions para automatizar o processo de build e disponibilização do ficheiro JAR.
A pipeline é executada automaticamente sempre que há um push para a branch main.

1. Checkout do código

A primeira etapa faz o checkout do código-fonte do repositório, garantindo que a pipeline tem acesso a todos os ficheiros necessários para o build.

name: Checkout code
uses: actions/checkout@v4
with:
  fetch-depth: 0

2. Configuração do JDK 21

Nesta etapa, é configurado o ambiente Java com a versão 21 do JDK, utilizando a distribuição Temurin, e ativada a cache do Maven para acelerar builds futuros.

name: Set up JDK 21
uses: actions/setup-java@v4
with:
  distribution: temurin
  java-version: '21'
  cache: maven

3. Compilação com Maven

O projeto é compilado através do Maven, que executa uma limpeza (clean) e cria o pacote (package), gerando o ficheiro .jar final.

name: Build with Maven
run: mvn -B clean package

4. Upload do artefacto JAR

Por fim, o artefacto resultante (ficheiro JAR) é armazenado nos artefactos do GitHub Actions, permitindo o seu download direto a partir da execução da pipeline.

name: Upload JAR artifact
uses: actions/upload-artifact@v4
with:
  name: todoapp-jar
  path: target/*.jar


## Project Structure

The sources of your App have the following structure:

```
src
├── main/frontend
│   └── themes
│       └── default
│           ├── styles.css
│           └── theme.json
├── main/java
│   └── [application package]
│       ├── base
│       │   └── ui
│       │       ├── component
│       │       │   └── ViewToolbar.java
│       │       ├── MainErrorHandler.java
│       │       └── MainLayout.java
│       ├── examplefeature
│       │   ├── ui
│       │   │   └── TaskListView.java
│       │   ├── Task.java
│       │   ├── TaskRepository.java
│       │   └── TaskService.java                
│       └── Application.java       
└── test/java
    └── [application package]
        └── examplefeature
           └── TaskServiceTest.java                 
```

The main entry point into the application is `Application.java`. This class contains the `main()` method that start up 
the Spring Boot application.

The skeleton follows a *feature-based package structure*, organizing code by *functional units* rather than traditional 
architectural layers. It includes two feature packages: `base` and `examplefeature`.

* The `base` package contains classes meant for reuse across different features, either through composition or 
  inheritance. You can use them as-is, tweak them to your needs, or remove them.
* The `examplefeature` package is an example feature package that demonstrates the structure. It represents a 
  *self-contained unit of functionality*, including UI components, business logic, data access, and an integration test.
  Once you create your own features, *you'll remove this package*.

The `src/main/frontend` directory contains an empty theme called `default`, based on the Lumo theme. It is activated in
the `Application` class, using the `@Theme` annotation.

## Starting in Development Mode

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

## Building for Production

To build the application in production mode, run:

```bash
./mvnw -Pproduction package
```

To build a Docker image, run:

```bash
docker build -t my-application:latest .
```

If you use commercial components, pass the license key as a build secret:

```bash
docker build --secret id=proKey,src=$HOME/.vaadin/proKey .
```

## Getting Started

The [Getting Started](https://vaadin.com/docs/latest/getting-started) guide will quickly familiarize you with your new
App implementation. You'll learn how to set up your development environment, understand the project 
structure, and find resources to help you add muscles to your skeleton — transforming it into a fully-featured 
application.
