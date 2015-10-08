#!/bin/bash

cd ./shiro3
chmod +x ./gradlew 
./gradlew :grails-shiro:test \
    :cli-tests:test \
    :wildcard-realm:integrationTest \
    --stacktrace
#    :native-session:test
#    :native-realm-only:test
#    :spring-filter:test
#    :default:test
#    :annotation-test:test
