#!/bin/bash

cd ./shiro3/grails-shiro
./gradlew test --stacktrace
testapp(){
  echo "include '$1', 'grails-shiro'" > ../settings.gradle
  cd ../$1
  ./gradlew test --stacktrace
  rm -Rf ../settings.gradle
}
#testapp "wildcard-realm"
#testapp "native-session"
#testapp "native-realm-only"
#testapp "spring-filter"
#testapp "cli-tests"
#testapp "default"
#testapp "annotation-test"
