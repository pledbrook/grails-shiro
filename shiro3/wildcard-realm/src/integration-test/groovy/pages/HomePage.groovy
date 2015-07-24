package pages

import geb.Page

class HomePage extends Page {
    static url = ""

    static at = {
        assert title == "Welcome to Grails"
        assert $("h1", text: "Welcome to Grails").size() > 0
        assert $("h2", text: "Available Controllers:").size() > 0
        return true
    }
}
