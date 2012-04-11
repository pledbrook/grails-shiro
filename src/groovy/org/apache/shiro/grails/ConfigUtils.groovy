package org.apache.shiro.grails

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.apache.shiro.cas.CasToken

/**
 * The Config utilies
 */
class ConfigUtils {
    private static config = ConfigurationHolder.config
    
    static def authBy = [:] //key is  AuthenticationToken's principal,AuthenticationToken class name
    
    static def getCasEnable() {
        config.security.shiro.cas.enable as Boolean
    }
    
    static def getShiroCasFilter() {
        "/shiro-cas=casFilter\n" + config.security.filter.filterChainDefinitions?:''
    }
    
    static def getLoginUrl() {
        def serverUrl = config.security.cas.serverUrl
        serverUrl.endsWith("/") ? serverUrl : (serverUrl+"/") +"login?service="+config.security.cas.serviceUrl
    }
    
    static def getLogoutUrl() {
        def serverUrl = config.security.cas.serverUrl
        serverUrl.endsWith("/") ? serverUrl : (serverUrl+"/") +"logout?service="+config.security.cas.serviceUrl
    }
    
    static def putPrincipal(authenticationToken) {
        authBy[authenticationToken.principal.toString()] = authenticationToken.class
    }
    static def isFromCas(principal) {
        authBy[principal.toString()] instanceof CasToken
    }
    static def removePrincipal(principal) {
        authBy.remove(principal.toString())
    }
}
