package test

import org.apache.shiro.authz.Permission

class CustomPermission implements Permission {
    private companyId

    CustomPermission(String id) {
        this.companyId = id.toLong()
    }

    CustomPermission(Long id) {
        this.companyId = id
    }

    boolean implies(Permission p) {
        return p instanceof CustomPermission && p.companyId == this.companyId
    }
}
