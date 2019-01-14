Grails 3 Shiro plugin
=====================

[![Build Status](https://api.travis-ci.org/pledbrook/grails-shiro.png)](http://travis-ci.org/pledbrook/grails-shiro)

Documentation is a work in progress.

## Installation
To install this plugin you need to add this to your `build.gradle` dependencies.

```
compile 'org.grails.plugins:grails-shiro:3.3.0'
```

## Configuration
To override default configuration of the shiro plugin you can simply add corresponding configuration keys to your `application.groovy` or `application.yaml` file.

```
grails {
  plugin {
    shirosecurity {
      session.mode = [null, 'native'] //default null
      filter {
        loginUrl = [url for your login page] //default "/auth/login"
        unauthorizedUrl = [url for your unauthorized page] //default "/auth/unauthorized"
        successUrl = [url for your success page] //default null
        basicAppName = [name to show in basic http auth] //not configured by default, it triggers basic auth
        filterChainDefinitions = optional, see [https://shiro.apache.org/static/1.4.0/apidocs/org/apache/shiro/spring/web/ShiroFilterFactoryBean.html#setFilterChainDefinitions-java.lang.String-]
      }
      authc.required = [boolean] //Get the config option that determines whether authentication is required for access control or not. By default, it is required.
      redirect.uri = [url for redirect when page needs authentication] //default "/auth/login?targetUri={urlRequested}"
    }
  }
}
```

## Available codegen scripts
To help you get started, plugin provides convenient way to generate required classes. You can use them with your `grails` command or grails wrapper `grailsw`.

`grails shiro-quick-start [--prefix your.package.name.ClassPrefix]` - creates basic domain classes, database realm to authenticate your users against database and security interceptor and auth controller. Default prefix is `shiro3.Shiro`.

`grails create-wildcard-realm [--prefix your.package.name.ClassPrefix]` - creates a new database realm from a template that only works with	wildcard permissions. Other types of permission are not supported.

`grails create-security-interceptor [--prefix your.package.name.ClassPrefix]` - creates a new security interceptor from a template.

`grails create-ldap-realm [--prefix your.package.name.ClassPrefix]` - creates a new ldap realm from a template.

`grails create-db-realm [--prefix your.package.name.ClassPrefix]` - creates a new database realm from a template that only works with database permissions. Other types of permission are not supported.

`grails create-auth-controller [--prefix your.package.name.ClassPrefix]` - creates a new authentication controller from a template.
