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
  
[Voltar ao sumário](#Sumário)

# Tools used

* IntelliJ IDEA Community Edition
* Docker
* Gradle
  
[Voltar ao sumário](#Sumário)

# Execução do projeto

Há várias maneiras de executar os projetos:

Executando tudo via docker-compose
Executando tudo via script de automação que eu disponibilizei (build.py)
Executando apenas os serviços de bancos de dados e message broker (Kafka) separadamente
Executando as aplicações manualmente via CLI (java -jar ou gradle bootRun ou via IntelliJ)
Para rodar as aplicações, será necessário ter instalado:

Docker
Java 17
Gradle 7.6 ou superior

## 01 - Execução geral via docker-compose

Basta executar o comando no diretório raiz do repositório:

docker-compose up --build -d

Obs.: para rodar tudo desta maneira, é necessário realizar o build das 5 aplicações, veja nos passos abaixo sobre como fazer isto.

## 02 - Execução geral via automação com script em Python

Basta executar o arquivo build.py. Para isto, é necessário ter o Python 3 instalado.

Para executar, basta apenas executar o seguinte comando no diretório raiz do repositório:

python build.py

Será realizado o build de todas as aplicações, removidos todos os containers e em sequência, será rodado o docker-compose.

## 03 - Executando os serviços de bancos de dados e Message Broker

Para que seja possível executar os serviços de bancos de dados e Message Broker, como MongoDB, PostgreSQL e Apache Kafka, basta ir no diretório raiz do repositório, onde encontra-se o arquivo docker-compose.yml e executar o comando:

docker-compose up --build -d order-db kafka product-db payment-db inventory-db

Como queremos rodar apenas os serviços de bancos de dados e Message Broker, é necessário informá-los no comando do docker-compose, caso contrário, as aplicações irão subir também.

Para parar todos os containers, basta rodar:

docker-compose down

Ou então:

docker stop ($docker ps -aq) docker container prune -f

## 04 - Executando manualmente via CLI
Voltar ao nível anterior

Antes da execução do projeto, realize o build da aplicação indo no diretório raiz e executando o comando:

gradle build -x test

Para executar os projetos com Gradle, basta entrar no diretório raiz de cada projeto, e executar o comando:

gradle bootRun

Ou então, entrar no diretório: build/libs e executar o comando:

java -jar nome_do_jar.jar

[Voltar ao sumário](#Sumário)
