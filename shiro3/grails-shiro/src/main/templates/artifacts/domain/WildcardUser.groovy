${packageLine}class ${domainPrefix}User {
    String username
    String passwordHash
    
    static hasMany = [ roles: ${domainPrefix}Role, permissions: String ]

    static constraints = {
        username(nullable: false, blank: false, unique: true)
    }
}
