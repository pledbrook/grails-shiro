class ShiroRolePermissionRel {
    ShiroRole role
    ShiroPermission permission
    String target
    String actions

    static constraints = {
        actions(nullable: false, blank: false)
    }
}
