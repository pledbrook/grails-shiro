#! /bin/bash
# first the sources
cp -rf ../shiro2/src/groovy/* src/main/groovy
cp -rf ../shiro2/src/java/* src/main/groovy
cp -rf ../shiro2/grails-app grails-app
cp -rf ../shiro2/ShiroGrailsPlugin.groovy src/main/groovy/shiro3 #this is the wrong path
# then the tests
cp -rf ../shiro2/test/unit src/test/groovy
mkdir -p src/integration-test/groovy
cp -rf ../shiro2/test/integration src/integration-test/groovy
# then templates / other resources
cp -rf ../shiro2/src/templates src/main/templates
