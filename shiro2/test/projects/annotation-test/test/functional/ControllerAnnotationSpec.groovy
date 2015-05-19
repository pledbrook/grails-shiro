import geb.spock.GebReportingSpec
import org.openqa.selenium.Cookie
import pages.LoginPage

class ControllerAnnotationSpec extends GebReportingSpec {

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