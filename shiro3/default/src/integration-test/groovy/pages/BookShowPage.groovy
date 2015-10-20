package pages

import geb.Page

class BookShowPage extends Page {
    static url = "http://localhost:8080/book/show"

    static at = {
        assert title == "Show Book"
        assert $("h1", text: "Show Book").size() > 0
        assert $("a", text: "sign out").size() > 0
        return true
    }
}
