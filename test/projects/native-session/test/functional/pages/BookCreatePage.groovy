package pages

import geb.Page

class BookCreatePage extends Page {
    static url = "book/create"

    static at = {
        assert title == "Book Create"
        assert $("h1", text: "Book Create").size() > 0
        assert $("a", text: "sign out").size() > 0
        return true
    }
    
    static content = {
        createButton(to: BookShowPage) { $("input", value: "Create") }
    }
}
