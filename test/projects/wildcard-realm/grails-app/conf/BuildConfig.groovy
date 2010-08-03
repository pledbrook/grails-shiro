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
    }
}
