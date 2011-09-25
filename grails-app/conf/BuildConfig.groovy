grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

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
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        compile 'org.apache.shiro:shiro-core:1.1.0',
                'org.apache.shiro:shiro-web:1.1.0',
                'org.apache.shiro:shiro-ehcache:1.1.0',
                'org.apache.shiro:shiro-quartz:1.1.0',
                'org.apache.shiro:shiro-aspectj:1.1.0',
                'org.apache.shiro:shiro-spring:1.1.0', {
            excludes 'ejb', 'jsf-api', 'jms', 'connector-api', 'ehcache-core', 'slf4j-api'
        }
    }
}

grails.project.dependency.distribution = {
    portal id: "beta", url: "http://beta.grails.org/"

    remoteRepository(id: "snapshots", url: "http://myserver:8081/artifactory/libs-snapshots-local")

    remoteRepository(id: "releases", url: "http://myserver:8081/artifactory/libs-releases-local") {
        authentication username: "admin", password: "password"
    }
}

