package annotation.test

import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles

@RequiresAuthentication
class SecuredService {

    def unrestricted() {
        return 'unrestricted'
    }

    @RequiresRoles('Administrator')
    def requiresRoleAdministrator() {
        return 'role admin'
    }


}
