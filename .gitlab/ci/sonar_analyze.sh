#!/bin/bash

mvn $MAVEN_CLI_OPTS clean package sonar:sonar \
    -Dsonar.issuesReport.html.enable=true \
    -Dsonar.analysis.mode=preview \
    -Dsonar.preview.excludePlugins=issueassign,scmstats

if [ $? -eq 0 ]; then
    echo "SonarQube code-analyze over."
    exit 0
else
    exit 1
fi
