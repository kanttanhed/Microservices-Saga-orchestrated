# Sumário

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
