#!/bin/bash

CORRECT_REPLICAS=4
CRASH_REPLICAS=0
BYZANTINE_REPLICAS=0
START_PORT=8080

CURRENT_PORT=$START_PORT

CMD="mvn install"
echo $CMD
eval $CMD

for I in $(seq 1 $CORRECT_REPLICAS)
do
    CMD="java -jar target/registerReplica-jar-with-dependencies.jar $CURRENT_PORT 0 &"
    echo $CMD
    eval $CMD
    CURRENT_PORT=$(( CURRENT_PORT + 1 ))
done

for i in $(seq 1 $CRASH_REPLICAS)
do
    CMD="java -jar target/registerReplica-jar-with-dependencies.jar $CURRENT_PORT 1 &"
    echo $CMD
    eval $CMD
    CURRENT_PORT=$(( CURRENT_PORT + 1 ))
done

for i in $(seq 1 BYZANTINE_REPLICAS)
do
    CMD="java -jar target/registerReplica-jar-with-dependencies.jar $CURRENT_PORT 2 &"
    echo $CMD
    eval $CMD
    CURRENT_PORT=$(( CURRENT_PORT + 1 ))
done
