#!/usr/bin/env bash

./gradlew build

ls $TRAVIS_BUILD_DIR/build/libs/*
