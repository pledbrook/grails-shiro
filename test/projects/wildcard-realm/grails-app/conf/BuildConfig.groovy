grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.plugin.location.shiro = "../../.."

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()
    }

    dependencies {
        test "org.codehaus.groovy.modules.http-builder:http-builder:0.5.0", {
            excludes "commons-logging", "httpclient", "groovy", "xml-apis"
        }
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
        test "org.gebish:geb-spock:0.9.1"

        test "org.seleniumhq.selenium:selenium-support:2.35.0"

        test "org.seleniumhq.selenium:selenium-chrome-driver:2.35.0"
        test "org.seleniumhq.selenium:selenium-firefox-driver:2.35.0"
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        build ":tomcat:$grailsVersion"
        compile ':cache:1.0.1'

        test(":spock:0.7") {
            exclude "spock-grails-support"
        }
        test ":geb:0.9.1"

    }
}
