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
        compile 'org.apache.shiro:shiro-all:1.2.1'

        optional 'org.apache.shiro:shiro-ehcache:1.2.1',
                'org.apache.shiro:shiro-quartz:1.2.1',
                'org.apache.shiro:shiro-cas:1.2.1', {
            excludes 'ejb', 'jsf-api', 'jms', 'connector-api', 'ehcache-core', 'slf4j-api', 'commons-logging'
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

