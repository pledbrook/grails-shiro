import annotation.test.Application
import geb.spock.GebReportingSpec
import org.openqa.selenium.Cookie
import pages.LoginPage
import spock.lang.Unroll

import grails.test.mixin.integration.Integration

@Integration(applicationClass=Application)
class ControllerAnnotationSpec extends GebReportingSpec {

    @Unroll
    def "RequiresAuthentication on controller class works on every action #theUrl"() {
        given:
        go 'auth/signOut'

        when:
        go theUrl

        then:
        at LoginPage

        where:
        theUrl        | val
        'book2/index'  | ''
        'book2/list'   | ''
        'book2/create' | ''
        'book2/save'   | ''
        'book2/show'   | ''
        'book2/edit'   | ''
        'book2/update' | ''
        'book2/delete' | ''
    }

    @Unroll
    def "User #user/#password with correct Roles has [#val] for url: #theUrl @#select"() {
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
        'admin'   | 'admin'    | 'book2/index'  | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book2/list'   | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book2/create' | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book2/save'   | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book2/show'   | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book2/edit'   | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book2/update' | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'book2/delete' | 'You do not have permission to access this page.'

        'test1'   | 'test1'    | 'book2/index'  | 'list'
        'test1'   | 'test1'    | 'book2/list'   | 'list'
        'test1'   | 'test1'    | 'book2/create' | 'create'
        'test1'   | 'test1'    | 'book2/save'   | 'save'
        'test1'   | 'test1'    | 'book2/show'   | 'show'
        'test1'   | 'test1'    | 'book2/edit'   | 'edit'
        'test1'   | 'test1'    | 'book2/update' | 'update'
        'test1'   | 'test1'    | 'book2/delete' | 'delete'

        'dilbert' | 'password' | 'book2/index'  | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'book2/list'   | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'book2/create' | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'book2/save'   | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'book2/show'   | 'show'
        'dilbert' | 'password' | 'book2/edit'   | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'book2/update' | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'book2/delete' | 'You do not have permission to access this page.'

    }
}