class Book {
    String title
    String author

    static constraints = {
        title(nullable: false, blank: false)
        author(nullable: false, blank: false)
    }
}
