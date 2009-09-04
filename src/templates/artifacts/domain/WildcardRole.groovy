class @domain.prefix@Role {
    String name

    static hasMany = [ users: @domain.prefix@User, permissions: String ]
    static belongsTo = @domain.prefix@User

    static constraints = {
        name(nullable: false, blank: false, unique: true)
    }
}
