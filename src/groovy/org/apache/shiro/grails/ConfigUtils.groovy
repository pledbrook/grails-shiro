package org.apache.shiro.grails

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.apache.shiro.cas.CasToken

/**
 * The Config utilies
 */
class ConfigUtils {
    private static innerConfig = null
    
    
    static getConfig() {
        if (innerConfig==null) 
            innerConfig = ConfigurationHolder.config
        innerConfig
    }
    static def authBy = [:] //key is  AuthenticationToken's principal,AuthenticationToken class name
    
    static def getCasEnable() {
        (config.security.shiro.cas.enable?:false).toBoolean()
    }
    
    static def getShiroCasFilter() {
        "/shiro-cas=casFilter\n" + config.security.shiro.filter.filterChainDefinitions?:''
    }
    
    static def getLoginUrl() {
        if (config.security.shiro.cas.loginUrl) {
            config.security.shiro.cas.loginUrl
        }else{
            def serverUrl = config.security.shiro.cas.serverUrl?:'https://localhost:8443/cas'
            serverUrl.endsWith("/") ? serverUrl : (serverUrl+"/") +"login?service="+config.security.shiro.cas.serviceUrl
        }
    }
    
    static def getLogoutUrl() {
        if (config.security.shiro.cas.logoutUrl) {
            config.security.shiro.cas.logoutUrl
        }else{
            def serverUrl = config.security.shiro.cas.serverUrl?:'https://localhost:8443/cas'
            serverUrl.endsWith("/") ? serverUrl : (serverUrl+"/") +"logout?service="+config.security.shiro.cas.serviceUrl
        }
    }
    
    static def putPrincipal(authenticationToken) {
        authBy[authenticationToken.principal.toString()] = authenticationToken.class
    }
    static def isFromCas(principal) {
        authBy[principal.toString()] == CasToken
    }
    static def removePrincipal(principal) {
        authBy.remove(principal.toString())
    }
}
