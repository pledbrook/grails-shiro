import geb.spock.GebReportingSpec
import org.openqa.selenium.Cookie
import pages.*
import spock.lang.Ignore

class ServiceMethodAnnotationSpec extends GebReportingSpec {

    def "When not logged in service method annotations return expected results"() {
        when:
        go theUrl

        then:
        if (select) {
            $(select).text().contains(value)
        } else {
            $().text().contains(value)
        }

        where:
        theUrl            | select  | value
        'test/unsecured'  | ''      | 'Unsecured: one'
        'test/guest'      | ''      | 'Guest: two'
        'test/role'       | 'title' | 'Login'
        'test/permission' | 'title' | 'Login'
        'test/user'       | 'title' | 'Login'
    }

    def "When logged in as user service method annotations return expected results"() {
        given:
        go 'auth/signOut'

        when:
        to LoginPage

        then:
        at LoginPage

        when:
        loginForm.username = user
        loginForm.password = password
        signIn.click()

        and:
        go theUrl

        then:
        if (select) {
            $(select).text().contains(value)
        } else {
            $().text().contains(value)
        }

        where:
        user      | password   | theUrl            | select | value
        'admin'   | 'admin'    | 'test/unsecured'  | ''     | 'Unsecured: one'
        'admin'   | 'admin'    | 'test/guest'      | ''     | 'Guest: two'
        'admin'   | 'admin'    | 'test/user'       | ''     | 'User: three'
        'admin'   | 'admin'    | 'test/role'       | ''     | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'test/permission' | ''     | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'test/role'       | ''     | 'Role: five'
        'dilbert' | 'password' | 'test/permission' | ''     | 'Permission: six'
    }

    def "When rememberMe is used and not authenticated this session"() {
        given:
        go 'auth/signOut'

        when:
        to LoginPage

        then:
        at LoginPage

        when:
        loginForm.username = 'admin'
        loginForm.password = 'admin'
        loginForm.rememberMe = true
        signIn.click()

        and:
        browser.driver.manage().deleteCookieNamed("JSESSIONID")
        go 'test/user'

        then:
        //has the rememberMe cookie set
        browser.driver.manage().cookies.find { Cookie cookie -> cookie.name == 'rememberMe' } != null
        $().text().contains('User: three')

        when:
        go 'test/authenticated'

        then:
        at LoginPage

        when:
        loginForm.username = 'admin'
        loginForm.password = 'admin'
        signIn.click()

        then:
        $().text().contains('Authenticated: four')
    }

    def "Wildcard permissions should work"() {
        given:
        go 'auth/signOut'

        when:
        go 'test/permission'

        then:
        at LoginPage

        when:
        //test1 user has permission book:*
        loginForm.username = 'test1'
        loginForm.password = 'test1'
        signIn.click()

        then:
        //should redirect
        $().text().contains('Permission: six')
    }

    def "Check secured Class requires authentication asks for login"() {
        given:
        go 'auth/signOut'

        when:
        go 'test/unrestricted'

        then:
        at LoginPage

        when:
        //test1 user has permission book:*
        loginForm.username = 'dilbert'
        loginForm.password = 'password'
        signIn.click()

        then:
        //should redirect
        $().text().contains('secure class: unrestricted')

        when:
        go 'test/administrator'

        then:
        $().text().contains('You do not have permission to access this page.')

    }

    def "Check secured Class requires authentication asks for login before refusing on role"() {
        given:
        go 'auth/signOut'

        when:
        go 'test/administrator'

        then:
        at LoginPage

        when:
        loginForm.username = 'dilbert'
        loginForm.password = 'password'
        signIn.click()

        then:
        $().text().contains('You do not have permission to access this page.')
    }

    def "RequiresAuthentication on controller class works on every action"() {
        given:
        go 'auth/signOut'

        when:
        go theUrl

        then:
        at LoginPage

        where:
        theUrl        | val
        'book/index'  | ''
        'book/list'   | ''
        'book/create' | ''
        'book/save'   | ''
        'book/show'   | ''
        'book/edit'   | ''
        'book/update' | ''
        'book/delete' | ''
    }

    def "Only a user with correct Roles can access controller methods"() {
        given:
        go 'auth/signOut'

        when:
        go theUrl

        then:
        at LoginPage

        when:
        //test1 user has role test
        loginForm.username = user
        loginForm.password = password
        signIn.click()

        then:
        println $().text() //.contains(val)

        where:
        user      | password   | theUrl        | val
        'admin'   | 'admin'    | 'book/index'  | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book/list'   | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book/create' | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book/save'   | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book/show'   | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book/edit'   | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book/update' | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book/delete' | 'You do not have permission to access this page.'

        'test1'   | 'test1'    | 'book/index'  | 'list'
        'test1'   | 'test1'    | 'book/list'   | 'list'
        'test1'   | 'test1'    | 'book/create' | 'create'
        'test1'   | 'test1'    | 'book/save'   | 'save'
        'test1'   | 'test1'    | 'book/show'   | 'show'
        'test1'   | 'test1'    | 'book/edit'   | 'edit'
        'test1'   | 'test1'    | 'book/update' | 'update'
        'test1'   | 'test1'    | 'book/delete' | 'delete'

        'dilbert' | 'password' | 'book/index'  | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'book/list'   | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'book/create' | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'book/save'   | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'book/show'   | 'show'
        'dilbert' | 'password' | 'book/edit'   | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'book/update' | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'book/delete' | 'You do not have permission to access this page.'

    }
}