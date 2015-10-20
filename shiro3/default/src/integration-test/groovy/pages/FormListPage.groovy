package pages

import geb.Page

class FormListPage extends Page {
    static url = "http://localhost:8080/form/list"

    static at = { title == "Form List" }
}
