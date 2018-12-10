#!/bin/bash

COMMITTER=$(git log -1 --format=%cE)
echo ${COMMITTER}

mvn $MAVEN_CLI_OPTS test

if [ $? -eq 0 ];
then
    echo "auto_test success."
    exit 0
else
    exit 1
fi