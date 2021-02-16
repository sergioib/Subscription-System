## Subscription System
In this document is explained characteristics of the Subscription Service which has been developed. In the fist section it explains the main decisions that have been made to meet the requirements setting in the challage. In the following sections are the frameworks and libraries that have been using in the development. At the end, there is a section which explains how to run/build/use the app.   

### 1.- Main decisions
This sectión will be split in three subsections one for each service that have been delveloped. To understand these decisions it is necesary to know the following data,

| Service | SLA response time | SLA monthly uptime|
| :--- | :---: | :---: |
| Public Service | Expected 100 ms | Expected 99.99% |
| Subscription Service | 150 ms | 99.99% |
| Email Service | 60 s | 85% |

#### 1.1.- Public service
The main porpuse of this service is expose an API RESTful for other application can made use of the system. Methods are exposed by this service are:

- Create a new subscription
- Cancel a subscription of the system
- Get a specific subscription by its identifier
- Get all subscription of the system

In the requirements it is said that for the creation of a new subscription it is necesary to call to the Subscription Service. Becuse of the that the response time of the Subcription Service it has been decided that the process has the following steps,

1. When a request is recived by the system with the data of a new subscription, Public service validates the parameters, if all are correct, it generates a new identifier for the subscription, else it returns a response of error.
2. The service makes an asynchronous request to the Subscription Service, sending the data of the subscription with the generated identifier.
3. Response to the client with the identifier generated.

It has been decided to make an asysnchronou request to the Subscription Service because its response time is higher than the let for the Public Serivce.With this solution the system meets with the requirements of a response time but it could cause other problems. 
- It could be that Subsctiption Service makes other validation of the data, and the request would not be accepted. The client of the sysmtem does not know this error.
- The Subscription Service could be down so the request would be lost.
- It could be an internal problem with the database of the service and it the subscription would be not saved correctly. It would be lost

Availability problems could be solved developing a retry system or using other process to communicate these systems, or using a queue of messages, how ever the data problems could not be solved.

For the other methods that are exposed by Public System it has been asumed that it connect directly with the database. Because it is imposible meeting the response time of 100 ms if it has to communicate with the Subscription Service that has 200 ms of response time.

The response time of the method for getting all the subscription of the system could be improved using a reactive driver to connect to the database. It will be generate a stream of data that with the capability of the framework used to make the API rest, Spring Webflux, it will reduce the response time.

#### 1.2.- Subscription service
This service has the logic necessary for the creation of a new subscription saving it in the database. It exponed a API rest secure with a basic authentication.

The process to create a new subscrtiption has the following data:

- If the request has been done for an no authenticated user, return a response error, else it validates the input data. If some data is incorrect it returns a response error in other case save the subscription in the database
- It send a connect with Email Service to send a email of confirmation. This communication is made by a queue of messages.
- Return an OK responte to the calling application.

The communication between the Subscription Service and the Email Service it has been made by a queue of messages because the response time of the Email Service is the 60 s while the Subscription Service has to be of 150 ms. In the communication it has been prefered using a queue of messages due to the fact that the availability of the Email Service is 85% so the probability of losing some communication is higher than in the other case. Using this system, althought the Email system would down, the message would be saved in the queue. Other thing positive of the development is that if the amount of emails to send it very high it is possible add a new instance of the Email Service and they works currently without problems.

#### 1.3.- Email service
It is a simple service that does not expose any endpoint to communicate directly with it. It only consume the message of a queue and send a email notification with the data of this message. 
Because of the processing time is very high for each message, it exists the risk of the queue of messages be completed. However this architecture lets add other instances of Email services without dificulty, so if the queue is almost full a new entity is deployed and there will be the double amount of capacity of processing.

### 2.- Frameworks used
In this section it will be listed the main frameworks that have been used to develep the system.

- **Spring Boot**, helps you to quickly build any application without having to worry about some basic configurations. Allows for easily connection with database and queue services. Facilitates the creation and testing of Java-based applications by providing a default setup for unit and integration tests
- **Spring Data JPA**, makes easy the implementation of JPA based repository. It provide pagination support, dynamic query execution and ability to integrate custom data access code
- **Spring WebFlux**, is a reative-stack web framework that let us the opportunity of developing a non-blocking API Rest.
- **Spring AMQP**, provides a high-level abstrancion for sending and receiving messages to the development of AMQP-based solutions
- **Spring Security**, is a customizable authentication and access-control framework
- **JUnit 5**, is a set of libraries that are using to create a unit test in Java application
- **Hibernate**, JPA implementation used by Spring Data JPA by default.
- **OpenAPI 3 with SwaggerUI**, defines a standard, language-agnostic interface to RESTful APIs with allows humans and computers to discover and understand the capabilities of tha service without access to source code or documentation. SwaggerUI let visualize being automatically generated form your OpenAPI

## 3.- Run/build/use the app
### 3.1- Building for development
To build the Subscription System for development you must build their three services. To build each service you must be in the folder where the associated pom is located and run the following command,

```
mvn clean package -P dev
```

### 3.2.- Running the Subscription Service for develoment
It is necessary to have a running containter with the database and other with de RabbitMQ server with a queue with name "subscriptionQueue" configurated. To achive this, there is a file in the path **/docker/app_dev.yml** that has the configuration necessary to up these containers running the following command.
```
docker-compose -f app_dev.yml up -d
```
When this cointiers are running, it is necessary launch the three services in the following order due to that the Public Service has the configuration to create the database.

1. **Public Service**
	```
	java -jar target/PublicService-0.0.1.jar
	```
2. **Subscription Service**
	```
	java -jar target/SubscriptionService-0.0.1.jar
	```
3. **Email Service**
	```
	java -jar target/EmailService-0.0.1.jar
	```

### 3.3.- Using Subscription Service in development
If you want to access to RabbitMQ Server to see the registred services, [http://localhost:15672](http://localhost:15672) in your browser. The credential to access are
- **User**: guest
- **Password**: guest

To do watch the API, [http://localhost:8885/swagger-ui.html](http://localhost:8885/swagger-ui.html) in your browser.

To access to the methods exposed are necesary the following request:
- **Create a new Subscription**,
**Request** -> POST to [http://localhost:8885/subscription/](http://localhost:8885/subscription/).
**Response** -> The id of the created subscription
``` 
curl -i -H "Content-Type: application/json" -X POST -d '{"subscriptionId":"1","email":"1","firstName":"asdf","gender":"MALE","dateOfBirth":"1192-11-27","consent":"true","newsletterId":"125"}' http://localhost:8885/subscription/
```
- **Get a Subscription by Id**,
**Request** -> GET to [http://localhost:8885/subscription/${SubscriptionId}](http://localhost:8885/subscription/${SubscriptionId}).
**Response** -> If the subscription exists, it will return the data of the subscription, else it will return a 400 Bad Request error explaining the origin of if
``` 
curl -i -X GET http://localhost:8885/subscription/${SubscriptonId}
```
- **Cancel a Subscription by Id**,
**Request** -> DELETE to [http://localhost:8885/subscription/${SubscriptionId}](http://localhost:8885/subscription/${SubscriptionId}). 
**Response** -> If the subscription exists, it will return a response 204 - No Content, else it will return a 400 - Bad Request error explaining the origin of it
``` 
curl -i -X DELETE http://localhost:8885/subscription/${SubscriptonId}
```
- **Get all subscriptions of the system**,
**Request** -> GET to [http://localhost:8885/subscription](http://localhost:8885/subscription). 
**Response** -> Return a list with all the subscripton of the system
``` 
curl -i -X GET http://localhost:8885/subscription
```

### 3.4.- Using Docker
You can dockerice the Subscription System. To achieve this, first build a docker image of services running the following command in the main folder of each service.

```
mvn clean package -P docker docker:build
```

After executing the below command, you must to go to the docker folder, and run:

```
docker-compose -f app.yml up -d
```
This command will create one container with postgresql database and another with the rabbitMQ server. It also create three more containers where the three services will be executed. In addition, it will be create a internal docker network which to possible the communication among all cointainers.

Only the port 8885 of the container where will be executed the Public service has been published to be reachable since a external network.

To do watch the API, [http//localhost:8885/swagger-ui.html](http//localhost:8080/swagger-ui.html) in your browser.

If you want to use the methos which have been exposed you have to use the same commands that are been explained in the previous section.

To stop it and remove the containers, run:

```
docker-compose -f app.yml down
```
