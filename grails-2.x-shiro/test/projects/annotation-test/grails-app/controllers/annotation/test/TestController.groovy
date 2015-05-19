package annotation.test

class TestController {
    def securedMethodsService
    def securedService

    def index = {}
    def show = { render "Show page" }
    def create = { render "Create page" }
    def edit = { render "Edit page" }
    def delete = { render "Delete page" }

    def hasRole = {}
    def lacksRole = {}
    def hasPermission = {}

    def unsecured = {
        render(view: 'simple', model: [msg: "Unsecured: " + securedMethodsService.methodOne()])
    }

    def guest = {
        render(view: 'simple', model: [msg: "Guest: " + securedMethodsService.methodTwo()])
    }

    def user = {
        render(view: 'simple', model: [msg: "User: " + securedMethodsService.methodThree()])
    }

    def authenticated = {
        render(view: 'simple', model: [msg: "Authenticated: " + securedMethodsService.methodFour()])
    }

    def role = {
        render(view: 'simple', model: [msg: "Role: " + securedMethodsService.methodFive()])
    }

    def permission = {
        render(view: 'simple', model: [msg: "Permission: " + securedMethodsService.methodSix()])
    }

    def unrestricted = {
        render(view: 'simple', model: [msg: "secure class: " + securedService.unrestricted()])
    }

    def administrator = {
        render(view: 'simple', model: [msg: "secure class: " + securedService.requiresRoleAdministrator()])
    }
}

