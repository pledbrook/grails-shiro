package pages

import geb.Page

class BookCreatePage extends Page {
    static url = "book/create"

    static at = {
        assert title == "Create Book"
        assert $("h1", text: "Create Book").size() > 0
        assert $("a", text: "sign out").size() > 0
        return true
    }
    
    static content = {
        createButton(to: BookShowPage) { $("input", value: "Create") }
    }
}
