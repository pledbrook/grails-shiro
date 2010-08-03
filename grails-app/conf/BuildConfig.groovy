grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
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
        grailsCentral()
        mavenCentral()
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        compile 'org.apache.shiro:shiro-core:1.0.0-incubating',
                'org.apache.shiro:shiro-web:1.0.0-incubating',
                'org.apache.shiro:shiro-ehcache:1.0.0-incubating',
                'org.apache.shiro:shiro-quartz:1.0.0-incubating',
                'org.apache.shiro:shiro-spring:1.0.0-incubating', {
            excludes 'ejb', 'jsf-api', 'jms', 'connector-api'
        }
    }
}
