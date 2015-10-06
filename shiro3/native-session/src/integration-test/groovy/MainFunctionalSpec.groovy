import geb.spock.GebReportingSpec
import pages.*

class MainFunctionalSpec extends GebReportingSpec {
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

    def "Test access to page that just requires authentication"() {
        when: "I go to the secured item page"
        go "itemSecured"
        page LoginPage

        then: "I am redirected to the login page"
        at LoginPage

        when: "I log in using a valid username and password"
        loginForm.username = "dilbert"
        loginForm.password = "password"
        signIn.click()

        then: "I am redirected to the the secured item page"
        $().text().contains("Secured item")
    }

    def "Test unauthorised access to secure page"() {
        given:
        login "dilbert", "password"

        when:
        go "book/show/1"

        then:
        verifyUnauthorised()
    }

    def "Test authorised access to role-protected page"() {
        given:
        login "admin", "admin"

        when:
        go "book/show/1"

        then:
        title == "Show Book"
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
        given:
        go "books"

        expect:
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
        $("h2")*.text() == [ "Comments", "Edit a user comment" ]
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
        $("h2")*.text() == [ "Comments", "Tags" ]
    }

    def "Test tag errors display correctly"() {
        given:
        login "test1", "test1"

        when: "User accesses page with an error in 'hasRole' tag"
        go "test/hasRole"

        then: "The error page is displayed with the correct name of the tag"
        $().text().contains("Tag [hasRole]")

        when: "User accesses page with an error in 'lacksRole' tag"
        go "test/lacksRole"

        then: "The error page is displayed with the correct name of the tag"
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
     * Test for GRAILSPLUGINS-1742.
     */
    def "Using flash does not break access control"() {
        given:
        login "admin", "admin", BookCreatePage

        when: "User creates a new book"
        $("form").title = "The Shining"
        $("form").author = "Stephen King"
        createButton.click()

        then: "The book details are displayed correctly"
        at BookShowPage
        $("tbody tr", 1).find("td", 1).text() == "The Shining"
        $("tbody tr", 2).find("td", 1).text() == "Stephen King"
    }

    /**
     * Logs into the application either via a target page that requires
     * authentication or by directly requesting the login page.
     */
    private login(username, password, targetPage = null, params = [:]) {
        if (targetPage) {
            go([*:params], targetPage.url)
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
