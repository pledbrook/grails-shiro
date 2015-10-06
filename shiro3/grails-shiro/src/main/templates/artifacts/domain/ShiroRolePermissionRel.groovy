${packageLine}class ${domainPrefix}RolePermissionRel {
    ${domainPrefix}Role role
    ${domainPrefix}Permission permission
    String target
    String actions

    static constraints = {
        actions(nullable: false, blank: false)
    }
}
