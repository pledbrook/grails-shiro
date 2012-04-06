@package.line@import org.apache.shiro.cas.CasAuthenticationException
import org.apache.shiro.authc.AccountException
import org.apache.shiro.authc.SimpleAuthenticationInfo
import org.apache.shiro.util.StringUtils
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.subject.SimplePrincipalCollection

import org.jasig.cas.client.authentication.AttributePrincipal
import org.jasig.cas.client.validation.*

/**
 * Simple realm that authenticates users against an CAS server.
 */
class @realm.name@ {
    static authTokenClass = org.apache.shiro.cas.CasToken
    private def ticketValidator = null
    def grailsApplication

    def authenticate(authToken) {
        log.info "Attempting to authenticate ${authToken.userId} in CAS realm..."

        def appConfig = grailsApplication.config
        def serverUrl = appConfig.security.shiro.cas.serverUrl ?: [ "https://localhost:8443/cas" ]
        def serviceUrl = appConfig.security.shiro.cas.serviceUrl
        
        if (!serviceUrl) {
            log.err(" the serviceUrl can not be empty.it should be http://host:port/mycontextpath/shiro-cas")
        }
        
        if (authToken == null) {
            throw new AccountException('cas tokens are not be null in this realm.')
        }
        String ticket = (String)authToken.getCredentials()
        if (!StringUtils.hasText(ticket)) {
            throw new AccountException('wrong cas token ticket:'+ticket)
        }
        
        if (ticketValidator==null) {
            ticketValidator = new Cas20ServiceTicketValidator(serverUrl)
        }

        try {
            // contact CAS server to validate service ticket
            Assertion casAssertion = ticketValidator.validate(ticket, serviceUrl)
            // get principal, user id and attributes
            AttributePrincipal casPrincipal = casAssertion.principal
            String userId = casPrincipal.name
            if (log.debugEnabled) {
                log.debug("Validate ticket : {} in CAS server : {} to retrieve user : {}", new Object[]{
                        ticket, serverUrl, userId
                })
            }

            Map<String, Object> attributes = casPrincipal.attributes
            // refresh authentication token (user id + remember me)
            casToken.setUserId(userId)
            String rememberMeStringValue = (String)attributes.get('longTermAuthenticationRequestTokenUsed')
            boolean isRemembered = rememberMeStringValue?.toBoolean()
            if (isRemembered) {
                casToken.setRememberMe(true)
            }
            // create simple authentication info
            List<Object> principals = CollectionUtils.asList(userId, attributes)
            PrincipalCollection principalCollection = new SimplePrincipalCollection(principals, this.toString())
            return new SimpleAuthenticationInfo(principalCollection, ticket)
        } catch (TicketValidationException e) { 
            throw new CasAuthenticationException("Unable to validate ticket [" + ticket + "]", e);
        }
        
    }
}
