grails.plugin.location.shiro = "../../.."
grails.project.work.dir = "work"

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        grailsHome()
        grailsPlugins()
        grailsCentral()
    }
    dependencies {
        test "org.seleniumhq.selenium:selenium-htmlunit-driver:2.0a6", {
            excludes "xml-apis", "commons-logging"
        }
        test "net.sourceforge.htmlunit:htmlunit:2.8", {
            excludes "xml-apis", "commons-logging"
        }
    }
}
