grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"

grails.plugin.location.shiro = "../../.."

//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits( "global" ) {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {        
        grailsPlugins()
        grailsHome()

    }
    dependencies {
        test "org.codehaus.groovy.modules.http-builder:http-builder:0.5.0", {
            excludes "commons-logging", "httpclient", "groovy", "xml-apis"
        }
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
        test "org.gebish:geb-spock:0.9.1"

        test "org.seleniumhq.selenium:selenium-support:2.40.0"

        test "org.seleniumhq.selenium:selenium-chrome-driver:2.40.0"
        test "org.seleniumhq.selenium:selenium-firefox-driver:2.40.0"
    }
    plugins {
        build ":tomcat:$grailsVersion"
        runtime ":hibernate:$grailsVersion"

        test(":spock:0.7") {
            exclude "spock-grails-support"
        }
        test ":geb:0.9.1"
    }

}
