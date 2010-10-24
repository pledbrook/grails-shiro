package pages

import geb.Page

class HomePage extends Page {
    static url = ""

    static at = {
        assert title == "Welcome to Grails"
        assert $("h1", text: "Welcome to Grails").size() > 0
        assert $("a", text: "AuthController").size() > 0
        assert $("a", text: "BookController").size() > 0
        return true
    }
}
