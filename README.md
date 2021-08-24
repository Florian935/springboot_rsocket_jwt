# Spring Boot project with RSocket JWT

The purpose of this project is to demonstrate how to secure a RSocket application with a JWT in the metadata send by a requester.

This project contains the 4 `modes` of RSocket exchanges:
- fire-and-forget
- request-response
- streaming
- channel 

The requester call the responder.

# Launch database

To launch the ___MongoDB database___, first, go to root and launch `docker-compose up --build ` (optionally with the `-d` argument to launch in background).

When the database is up, you can use a client (for example ___MongoDB Compass___) to access to the database with a GUI. Create the `jwt_rsocket` database and then create a `user` collection.

# RSocket client

The easiest way to test the responder is to use the RSocket client.

To interact with the responder, you need to be authenticated because security is added for each routes with differents roles.
You can launch the command bellow to authenticate against the responder.

To obtain the ___JWT___, you need first to launch the docker-compose, create a database in MongoDB, a collection ___user___ and documents into this collection to get users registered for the authentication and the generation of the ___JWT___.

When the user collection is created, launch the `requester` application. Before launch, uncomment in the `Application class` the `init() method` which save the 2 necessaries users (user and admin).

Then, you can launch the `responder` application.

When the 2 applications are launched, before initiate any interaction, you need to get the token needed for authentication against the responder application.

# Generate the JWT

To generate the token, you need to make an ___HTTP GET Request___ on the requester app to this URI: `http://localhost:8081/api/v1.0/authenticate`
The response return the token.

After, you can initiate any of the 4 interactions RSocket by adding in the header of HTTP request the `Authorization` header and for value the generated token prefixed by `Bearer` (Eg: Bearer your-token).

## To download the RSocket Client CLI
  `wget -O rsc.jar https://github.com/making/rsc/releases/download/0.9.1/rsc-0.9.1.jar`

## To make the client easier to work with, set an alias
  ``alias rsc='java -jar rsc.jar'```

## To use the client to do request-response against a server on tcp://localhost:7000
  `rsc --debug --authBearer *the-jwt-token* --request --data *data-to-send* --route request-response.{id} tcp://localhost:7000`

## To use the client to do fire-and-forget against a server on tcp://localhost:7000
  `rsc --debug --authBearer *the-jwt-token* --fnf --data *data-to-send* --route fire-and-forget tcp://localhost:7000`

## To use the client to do stream against a server on tcp://localhost:7000
  `rsc --debug --authBearer *the-jwt-token* --stream --data *data-to-send* --route request-stream tcp://localhost:7000`

## To use the client to do channel against a server on tcp://localhost:7000
  `rsc --debug --authBearer *the-jwt-token* --channel --data - --route channel tcp://localhost:7000`

After this command, you can send input to have a stream-stream interaction in the command prompt.

# Integration test

You can launch test by launching the `mvn clean integration-test` command.