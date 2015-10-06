${packageLine}class ${domainPrefix}UserPermissionRel {
    ${domainPrefix}User user
    ${domainPrefix}Permission permission
    String target
    String actions

    static constraints = {
        target(nullable: true, blank: false)
        actions(nullable: false, blank: false)
    }
}
