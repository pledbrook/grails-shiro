${packageLine}class ${domainPrefix}Role {
    String name

    static hasMany = [ users: ${domainPrefix}User, permissions: String ]
    static belongsTo = ${domainPrefix}User

    static constraints = {
        name(nullable: false, blank: false, unique: true)
    }
}
