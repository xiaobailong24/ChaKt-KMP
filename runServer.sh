#!/bin/bash

./gradlew :server:jvmRun -DmainClass=ApplicationKt --quiet 2>&1 | tee -a server.log