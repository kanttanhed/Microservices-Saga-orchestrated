# Status the project: in progress

# Summary 

- [Proposed Architecture](#ProposedArchitecture)
- [Technologies](#Technologies)
- [Tools Used](#Toolsused)
  
# Proposed Architecture

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://github.com/kanttanhed/Microservices-Saga-orchestrated/blob/main/content/Architecture.png">
  <source media="(prefers-color-scheme: light)" srcset="https://github.com/kanttanhed/Microservices-Saga-orchestrated/blob/main/content/Architecture.png">
  <img alt="Shows an illustrated sun in light mode and a moon with stars in dark mode." src="https://github.com/kanttanhed/Microservices-Saga-orchestrated/blob/main/content/Architecture.png">
</picture>

In our architecture, we will have 5 services:

* **Order-Service:** microservice responsible only for generating an initial order and receiving a notification. Here we have REST endpoints to start the process and retrieve data from events. The database used will be MongoDB.

* **Orchestrator-Service:** microservice responsible for orchestrating the entire Saga execution flow, it will know which microservice was executed and in which state, and which microservice will be sent to next, this microservice will also save the process from events. This service does not have a database.

* **Product Validation Service:** microservice responsible for validating whether the product specified in the order exists and is valid. This microservice will store a product validation for an order ID. The database used is PostgreSQL..

* **Payment Service:** microservice responsible for making a payment based on the unit values and details informed in the order. This microservice will store the payment information for an order. The database used is PostgreSQL.

* **Inventory Service:** microservice responsible for downloading the stock of products from an order. This microservice will store the download information of a product for an order ID. The database used is PostgreSQL.

All architecture services will be uploaded through the docker-compose.yml file.

# Technologies
* Java 17
* Spring Boot 3
* Apache Kafka
* API REST
* PostgreSQL
* MongoDB
* Docker
* docker-compose
* Redpanda Console
  
[Return to summary](#Summary)

# Tools used

* IntelliJ IDEA Community Edition
* Docker
* Gradle
  
[Voltar ao sumário](#Sumário)

# Execução do projeto

There are several ways to execute projects:

Running everything via docker-compose
Running everything via the automation script I made available (build.py)
Running only the database and message broker (Kafka) services separately
Running applications manually via CLI (java -jar or gradle bootRun or via IntelliJ)
To run the applications, you will need to have installed:

Docker
Java 17
Gradle 7.6 or higher

## 01 - General execution via docker-compose

Just run the command in the repository's root directory:

docker-compose up --build -d

Note: to run everything this way, it is necessary to build the 5 applications, see the steps below on how to do this.

## 02 - General execution with Python script

Just run the build.py file. To do this, you need to have Python 3 installed.

To execute, simply run the following command in the repository's root directory:

python build.py

All applications will be built, all containers will be removed and, subsequently, docker-compose will be run.

## 03 - Running database and message broker services

Para que seja possível executar os serviços de bancos de dados e Message Broker, como MongoDB, PostgreSQL e Apache Kafka, basta ir no diretório raiz do repositório, onde encontra-se o arquivo docker-compose.yml e executar o comando:

docker-compose up --build -d order-db kafka product-db payment-db inventory-db

Como queremos rodar apenas os serviços de bancos de dados e Message Broker, é necessário informá-los no comando do docker-compose, caso contrário, as aplicações irão subir também.

Para parar todos os containers, basta rodar:

docker-compose down

Ou então:

docker stop ($docker ps -aq) docker container prune -f

## 04 - Running manually via CLI
Voltar ao nível anterior

Antes da execução do projeto, realize o build da aplicação indo no diretório raiz e executando o comando:

gradle build -x test

Para executar os projetos com Gradle, basta entrar no diretório raiz de cada projeto, e executar o comando:

gradle bootRun

Ou então, entrar no diretório: build/libs e executar o comando:

java -jar nome_do_jar.jar

[Return to summary](#Summary)

# Project execution

To access the applications and place an order, simply access the URL:

http://localhost:3000/swagger-ui.html

You will reach this page:


Applications will run on the following ports:

- Order-Service: 3000
- Orchestrator-Service: 8080
- Product-Validation-Service: 8090
- Payment-Service: 8091
- Inventory-Service: 8092
- Apache Kafka: 9092
- Redpanda Console: 8081
- PostgreSQL (Product-DB): 5432
- PostgreSQL (Payment-DB): 5433
- PostgreSQL (Inventory-DB): 5434
- MongoDB (Order-DB): 27017

[Return to summary](#Summary)
