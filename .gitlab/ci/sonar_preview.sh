#!/bin/bash

mvn $MAVEN_CLI_OPTS verify sonar:sonar \
  -Dsonar.analysis.mode=preview \
  -Dsonar.gitlab.project_id=$CI_PROJECT_ID \
  -Dsonar.gitlab.commit_sha=$CI_COMMIT_SHA \
  -Dsonar.gitlab.ref_name=$CI_COMMIT_REF_NAME

if [ $? -eq 0 ];
then
    echo "SonarQube code-analyze-preview over."
    exit 0
else
    exit 1
fi
