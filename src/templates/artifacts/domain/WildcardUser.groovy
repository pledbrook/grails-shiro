@package.line@class @domain.prefix@User {
    String username
    String passwordHash
    
    static hasMany = [ roles: @domain.prefix@Role, permissions: String ]

    static constraints = {
        username(nullable: false, blank: false)
    }
}
