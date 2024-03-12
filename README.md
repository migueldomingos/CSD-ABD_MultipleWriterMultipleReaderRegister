# CSD - ABD Multiple Write Multiple Reader Distributed Register

This project is the basis of an exercise the for the first lab class of the Dependable Distributed Systems (Confiabilidade de Sistemas Distribuidos - CSD) graduate course.
In this Java project we have the code of a server that contains a register (simple float value) and the code of clients who can interact with the servers.

The system model we simulate is one where servers can fail, clients are spread across the world, and the network is asynchronous.
While all clients can read the values in server registers only one client can write.

The abstraction provided to an end user is that of connecting to a single server; that it, the end user does not see the potential discrepancies in the state of the servers and successive requests get more updated responses.
This is achieved through the use of response quorums.

## Main Objective

The client code assumes a crash fault model, where failed replicas do not respond to requests.
To support this fault model, the client just needs to ensure there is an overlap between the read and write quorums and between write quorums.

However, under a Byzantine fault model more nuance must be taken into consideration because the faulty nodes can produce arbitrary results that can fool the client.
The objective of this exercise is to modify the client code to support byzantine faults.

## Side Objectives

Study the effects of different quorum sizes and fault thresholds in the performance of the system.
Can we trigger a state inconsistency if we choose wrong quorum sizes?

## Potential Homework

The service provided only supports a single writer, however, with small modifications to the client protocol we could implement the ABD protocol, allowing multiple writters to update the registers concurrently.
The algorithm is described in the paper: [Sharing memory robustly in message-passing systems](https://dl.acm.org/doi/10.1145/200836.200869)

How good is the performance of ABD when compared with our baseline protocol?

## Technical Description

### Configurations

In config/config.properties the properties for running the client are specified.
These include how many servers are running and their ports and what is the size of the read and write quorums.

In config/log4j2.xml we define the configuration of the logger.
By default the logging is very fine grained. If this becomes bothersome, change the root level from "trace" to "info" or "warn".

### Launch

There is a script called launchReplicas.sh that creates a jar of the project and runs the replicas.
By default, the first replica starts at port 8080 and the following replicas have contiguous ports.
Here we can define how many replicas are correct, crash faulty and byzantine.

### Replicas

The class ReplicaMain launches the replicas.
This main receives as arguments the port of the replica and the type of register it runs.

All replicas run in localhost.
The types of registers that can be run are: Correct, Crash and Byzantine.
Correct registers act accordingly. They store the values submitted by the writer and have a monotonically increasing timestamp/sequence number
Crash registers sleep forever.
Byzantine registers ignore the writer and in read requests send an arbitrary value and the highest possible timestamp.

To simulate latency between the client and the servers, the replicas wait an indefinite amount of time before executing the client operations and before responding to the client.

### Client

The client class is run through the ClientMain class.
It reads the properties from config/config.properties and issues read and write operations to the replicas following the quorum sizes in the properties.

The operations issued in the ClientMain are very lackluster and insufficient for a good evaluation of the system.
Be sure to think of some interesting ways to strain the system and find where it breaks.
