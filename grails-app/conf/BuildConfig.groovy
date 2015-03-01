grails.project.work.dir = 'target'

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        compile ('org.apache.shiro:shiro-core:1.2.3',
                  'org.apache.shiro:shiro-web:1.2.3',
                  'org.apache.shiro:shiro-spring:1.2.3',
                  'org.apache.shiro:shiro-ehcache:1.2.3',
                  'org.apache.shiro:shiro-quartz:1.2.3') {
            excludes 'ejb', 'jsf-api', 'servlet-api', 'jsp-api', 'jstl', 'jms',
                     'connector-api', 'ehcache-core', 'slf4j-api', 'commons-logging'
        }
    }

    plugins {
        runtime ":hibernate4:4.3.6.1", {
            export = false
        }

        build ':release:3.0.1', ':rest-client-builder:1.0.3', {
            export = false
        }
    }
}
