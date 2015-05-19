class Item {
    String name
    String colour

    static constraints = {
        name(nullable: false, blank: false)
        colour(nullable: false, blank: false)
    }
}
