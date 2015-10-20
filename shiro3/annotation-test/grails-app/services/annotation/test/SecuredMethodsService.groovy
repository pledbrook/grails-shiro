package annotation.test

import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresGuest
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.apache.shiro.authz.annotation.RequiresRoles
import org.apache.shiro.authz.annotation.RequiresUser

class SecuredMethodsService {

    def methodOne() {
        return 'one'
    }

    @RequiresGuest
    def methodTwo() {
        return 'two'
    }

    @RequiresUser
    def methodThree() {
        return 'three'
    }

    @RequiresAuthentication
    def methodFour() {
        return 'four'
    }

    @RequiresRoles('User')
    def methodFive() {
        return 'five'
    }

    @RequiresPermissions("book2:view")
    def methodSix() {
        return 'six'
    }
}