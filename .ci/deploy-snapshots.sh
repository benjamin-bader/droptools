#!/bin/bash

if [ "$CI" != "true" ]
then
  echo "Not a CI build"
  exit 1
fi

if ! [[ $TRAVIS_REPO_SLUG =~ benjamin-bader/droptools ]]
then
  echo "Wrong repo"
  exit 1
fi

if [ "$TRAVIS_BRANCH" != "master" ]
then
  echo "Wrong branch"
  exit 1
fi

if [ "$TRAVIS_PULL_REQUEST" != "false" ]
then
  echo "Pull request"
  exit 1
fi

if [ ! -z $(awk '/^VERSION=/ && !/SNAPSHOT$/' gradle.properties) ]
then
  echo "Not a snapshot"
  exit 1
fi

./gradlew uploadArchives
