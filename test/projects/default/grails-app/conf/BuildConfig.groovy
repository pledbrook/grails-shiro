grails.plugin.location.shiro = "../../.."
grails.project.work.dir = "target"

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        grailsHome()
        grailsPlugins()
        grailsCentral()
    }
    dependencies {
        test "hsqldb:hsqldb:1.8.0.10", "org.codehaus.geb:geb-spock:0.6.3"
        test "org.codehaus.groovy.modules.http-builder:http-builder:0.5.0", {
            excludes "commons-logging", "httpclient", "groovy", "xml-apis"
        }
        test "org.seleniumhq.selenium:selenium-htmlunit-driver:2.0a6", {
            excludes "htmlunit"
        }
        test "net.sourceforge.htmlunit:htmlunit:2.8", {
            excludes "xml-apis", "commons-logging"
        }
    }
}
