grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    inherits "global"
    log "warn"
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes
        compile ('org.apache.shiro:shiro-core:1.2.2',
                'org.apache.shiro:shiro-web:1.2.2',
                'org.apache.shiro:shiro-spring:1.2.2',
                'org.apache.shiro:shiro-ehcache:1.2.2',
                'org.apache.shiro:shiro-quartz:1.2.2',
                'org.apache.shiro:shiro-cas:1.2.2') {
            excludes 'ejb', 'jsf-api', 'servlet-api', 'jsp-api', 'jstl', 'jms',
                    'connector-api', 'ehcache-core', 'slf4j-api', 'commons-logging'

        }
    }
    plugins {
        runtime ":hibernate:3.6.10.6"
        build (":tomcat:7.0.47",
                ":release:2.2.1") {
            export = false
        }
    }
}


