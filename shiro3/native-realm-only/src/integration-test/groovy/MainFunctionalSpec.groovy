import geb.spock.GebReportingSpec
import pages.BookCreatePage
import pages.LoginPage

class MainFunctionalSpec extends GebReportingSpec {
    /**
     * Tests the access control based on the custom realm.
     */
    void testAccessControl() {
        // Any user can log in with any password.
        when:
        go LoginPage.url

        then:
        at LoginPage

        when:
        loginForm.username = "test1"
        loginForm.password = "test1"
        signIn.click()

        then:
        $().text().contains('Welcome to Grails')

        // The custom realm always allows access to role-protected
        // resources, so test that now.
        when:
        go BookCreatePage.url

        then:
        at BookCreatePage

        when:
        go "book/edit/1"

        then:
        title == "Edit Book"
        $().text().contains('Edit Book')

        // Conversely, the realm does not allow access to any resource
        // protected by one or more permissions.
        when:
        go "book/list"

        then:
        $().text().contains('You do not have permission to access this page.')
    }
}
