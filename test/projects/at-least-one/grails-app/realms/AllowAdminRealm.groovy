import org.apache.shiro.authc.UnknownAccountException
import org.apache.shiro.authc.SimpleAccount

class AllowAdminRealm {
    static authTokenClass = org.apache.shiro.authc.UsernamePasswordToken

    def authenticate(authToken) {
        log.info "Attempting to authenticate ${authToken.username} in AllowAdmin realm..."

        def username = authToken.username
        if (username == "admin") {
            return new SimpleAccount(authToken.username, "admin", "AllowAdminRealm")
        }
        else {
            throw new UnknownAccountException("No account found for user [${username}]")
        }
    }

    def hasRole(principal, roleName) {
        return principal == "admin"
    }

    def hasAllRoles(principal, roles) {
        return principal == "admin"
    }

    def isPermitted(principal, requiredPermission) {
        return principal == "admin"
    }
}
