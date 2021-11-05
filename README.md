# Jakarta gRPC To Do List Example Application

## Overview

This example is a simple task management application which allows users to add, complete,
filter and delete tasks within a list.

### Clients

The example currently provides four JavaFX client implementations:

1. Legacy gRPC Java and Protobuf-based client that demonstrates how much overhead and boilerplate code is required in order to implement gRPC clients the "standard" way.

2. Helidon gRPC and Protobuf-based client that demonstrates how the above can be simplified even when using Protobuf

3. Helidon gRPC and JSON-based client that simplifies things even further by replacing Protobuf with JSON marshalling. 

4. Helidon gRPC and POF-based client that simplifies things even further by replacing Protobuf with POF marshalling.

Any number of the clients can be run and will receive all events from other clients as
tasks are created, updated, completed or removed.

### Server Implementation

There is only one server implementation, which all the clients connect to and use to manipulate tasks. The tasks themselves are stored in Coherence, but that's largely irrelevant for the purpose of this demo, and is primarily done as a convenience, to avoid the need to set up a separate data store (and to reuse the existing demo code).

The server exposes four different endpoints, one for each client, which effectively all do the same operations, but use different payload formats for request and response messages.

## Prerequisites

In order to build and run the examples, you must have the following installed:

* Maven 3.6.3+
* Java 11+

## Build + Run Instructions
                    
To build everything, simply run
```bash
mvn clean install
```
in the root directory.

To run the server, execute the following command in the `helidon-server` subdirectory:
```bash
mvn exec:exec
```

To run each of the clients, execute the following command in the corresponding subdirectory:
```bash
mvn javafx:run
```

## References

* [Helidon](https://helidon.io/)
* [Coherence CE](https://coherence.community/)




