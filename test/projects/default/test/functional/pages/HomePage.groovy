package pages

import geb.Page

class HomePage extends Page {
    static url = ""

    static at = {
        title == "Welcome to Grails"
        $("h1", text: "Welcome to Grails").size() > 0
        $("a", text: "org.example.auth.AuthController").size() > 0
        $("a", text: "BookController").size() > 0
        return true
    }
}
