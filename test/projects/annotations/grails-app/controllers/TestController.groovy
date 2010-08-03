class TestController {
    def myService
    def mySecureService

    def index = { }
    def show = { render "Show page" }
    def create = { render "Create page" }
    def edit = { render "Edit page" }
    def delete = { render "Delete page" }

    def hasRole = {}
    def lacksRole = {}
    def hasPermission = {}

    def unsecured = {
        render "Unsecured: " + myService.methodOne()
    }

    def guest = {
        render "Guest: " + myService.methodTwo()
    }

    def user = {
        render "User: " + myService.methodThree()
    }

    def authenticated = {
        render "Authenticated: " + myService.methodFour()
    }

    def role = {
        render "Role: " + myService.methodFive()
    }

    def otherRole = {
        render "Role: " + myService.authMethodFive()
    }

    def permission = {
        render "Permission: " + myService.methodSix()
    }

    def otherPermission = {
        render "Permission: " + myService.userMethodSix()
    }

    def secureAdd = {
        render "Secured: " + mySecureService.addNumbers(2, 3)
    }

    def secureOne = {
        render "Secured: " + mySecureService.methodOne()
    }

    def secureTwo = {
        render "Secured: " + mySecureService.methodTwo()
    }

    def secureThree = {
        render "Secured: " + mySecureService.methodThree()
    }

    def secureFour = {
        render "Secured: " + mySecureService.methodFour()
    }
}
