import grails.plugin.geb.GebSpec
import pages.BookListPage
import pages.HomePage
import pages.LoginPage

class MainFunctionalSpec extends GebSpec {
    def "Test login page"() {
        when:
        to LoginPage

        then:
        at LoginPage
        !loginForm.username
        !loginForm.password
    }

    def "Test enter no password"() {
        given:
        to LoginPage

        when:
        loginForm.username = "admin"
        signIn.click()

        then:
        at LoginPage
        loginForm.username == "admin"
        !loginForm.password
        message.text() == "Invalid username and/or password"
    }

    def "Test invalid password"() {
        given:
        to LoginPage

        when:
        loginForm.username = "admin"
        signIn.click()

        then:
        at LoginPage
        loginForm.username == "admin"
        !loginForm.password
        message.text() == "Invalid username and/or password"
    }

    def "Test valid login"() {
        given:
        to LoginPage

        when:
        loginForm.username = "admin"
        loginForm.password = "admin"
        signIn.click(HomePage)

        then:
        at HomePage
        $().text().contains("Welcome back admin!")
    }

    def "Test access to secure page"() {
        given:
        login "admin", "admin"

        when:
        go "itemSecured"

        then:
        $().text().contains("Secured item")
    }

    def "Test unauthorised access to secure page"() {
        given:
        login "dilbert", "password"

        when:
        go "itemSecured"

        then:
        verifyUnauthorised()
    }

    def "Test logging out"() {
        given:
        login "admin", "admin"

        when:
        go "auth/signOut"

        then:
        at HomePage
        !$().text().contains("Welcome back admin!")
    }

    def "Test unauthenticated access to page secured via permissions"() {
        when:
        to BookListPage
        page LoginPage

        then:
        at LoginPage

        when:
        loginForm.username = "admin"
        loginForm.password = "admin"
        signIn.click(BookListPage)

        then:
        at BookListPage
        $("tbody tr").size() == 3
        $().text().contains("Logged in as: admin")
        $("a", text: "sign out").size() == 1
    }

    def "Test sign out link"() {
        given:
        login "admin", "admin", BookListPage

        when:
        $("a", text: "sign out").click()
        page HomePage

        then:
        at HomePage
        !$().text().contains("Welcome back")
    }

    def "Test user role"() {
        when:
        login "dilbert", "password", BookListPage

        then:
        at BookListPage

        when:
        go "book/create"

        then:
        title == "Create Book"

        when:
        go "book/edit"

        then:
        verifyUnauthorised()

        when:
        go "book/save"

        then:
        verifyUnauthorised()

        when:
        go "book/update"

        then:
        verifyUnauthorised()

        when:
        go "book/delete"

        then:
        verifyUnauthorised()
    }

    def "Test authentication redirect with query string"() {
        when: "I access the book list page with a sort query string"
        login "admin", "admin", BookListPage, [sort: 'title', order: 'desc']

        then: "The list of books is displayed in the correct order"
        at BookListPage
        $("tbody tr").size() == 3
        $("tbody tr")*.find("td", 1)*.text() == [ "Misery", "Guns, Germs, and Steel", "Colossus" ]
    }

    def "Test admin has access to all pages"() {
        when: "Admin accesses the 'create' page"
        login "admin", "admin"
        go "book/create"

        then: "The 'create' page is displayed"
        $("h1", text: "Create Book").size() == 1

        when: "Admin accesses the 'edit' page"
        go "book/edit/1"

        then: "The 'edit' page is displayed"
        $("h1", text: "Edit Book").size() == 1
    }

    def "Test user has access to JsecBasicPermission protected pages"() {
        given:
        login "test1", "test1"

        when: "User accesses 'create' page"
        go "test/create"

        then: "The 'create' page is displayed"
        $().text().contains("Create page")

        when: "User accesses 'edit' page"
        go "test/edit"

        then: "User is denied access"
        verifyUnauthorised()

        when: "User accesses 'delete' page"
        go "test/delete"

        then: "User is denied access"
        verifyUnauthorised()
    }
    
    def "Test hasPermission tag with JsecBasicPermission"() {
        given:
        login "test1", "test1"

        when: "User accesses the index page"
        go "test/index"

        then: "The comments are displayed, but not the tags"
        $("div.list h2")*.text() == [ "Comments", "Edit a user comment" ]
    }

    def "Test user has access to JsecBasicPermission protected pages - different user"() {
        given:
        login "test2", "test2"

        when: "User accesses 'create' page"
        go "test/create"

        then: "User is denied access"
        verifyUnauthorised()

        when: "User accesses 'edit' page"
        go "test/edit"

        then: "The 'edit' page is displayed"
        $().text().contains("Edit page")

        when: "User accesses 'delete' page"
        go "test/delete"

        then: "The 'delete' page is displayed"
        $().text().contains("Delete page")

        when: "User accesses the index page"
        go "test/index"

        then: "The comments and tags are displayed, but not the comment editing"
        $("div.list h2")*.text() == [ "Comments", "Tags" ]
    }

    def "Test tag errors display correctly"() {
        given:
        login "test1", "test1"

        when: "User accesses page with an error in 'hasRole' tag"
        go "test/hasRole"

        then: "The error page is displayed with the correct name of the tag"
        // Only works with HtmlUnit driver
        browser.driver.lastPage().webResponse.statusCode == 500
        $().text().contains("Tag [hasRole]")

        when: "User accesses page with an error in 'lacksRole' tag"
        go "test/lacksRole"

        then: "The error page is displayed with the correct name of the tag"
        browser.driver.lastPage().webResponse.statusCode == 500
        $().text().contains("Tag [lacksRole]")
    }

    def "Test permission() method in access control"() {
        given:
        login "test1", "test1"

        when: "User accesses wildcard 'index' page"
        go "wildcard/index"

        then: "The page is displayed"
        $().text().contains("Wildcard index page")

        when: "User accesses 'one' page"
        go "wildcard/one"

        then: "The page is displayed"
        $().text().contains("Wildcard page one")

        when: "User accesses 'two' page"
        go "wildcard/two"

        then: "Access is denied"
        verifyUnauthorised()
    }

    def "Test permission() method in access control - different user"() {
        given:
        login "test2", "test2"

        when: "User accesses wildcard 'index' page"
        go "wildcard/index"

        then: "The page is displayed"
        $().text().contains("Wildcard index page")

        when: "User accesses 'one' page"
        go "wildcard/one"

        then: "Access is denied"
        verifyUnauthorised()

        when: "User accesses 'two' page"
        go "wildcard/two"

        then: "The page is displayed"
        $().text().contains("Wildcard page two")
    }

    /**
     * Logs into the application either via a target page that requires
     * authentication or by directly requesting the login page.
     */
    private login(username, password, targetPage = null, params = [:]) {
        if (targetPage) {
            to([*:params], targetPage)
            page LoginPage
        }
        else {
            to LoginPage
        }

        loginForm.username = username
        loginForm.password = password

        if (targetPage) signIn.click(targetPage)
        else signIn.click(HomePage)
    }

    private boolean verifyUnauthorised() {
        return $().text().contains("You do not have permission to access this page.")
    }
}
