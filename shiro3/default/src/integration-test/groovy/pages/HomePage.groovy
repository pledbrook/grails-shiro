package pages

import geb.Page

class HomePage extends Page {
    static url = "http://localhost:8080/"

    static at = {
        title == "Welcome to Grails"
        $("h1", text: "Welcome to Grails").size() > 0
        $("a", text: "AuthController").size() > 0
        $("a", text: "BookController").size() > 0
        return true
    }
}
