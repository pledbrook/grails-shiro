class TestController {
    def index = { }
    def show = { render "Show page" }
    def create = { render "Create page" }
    def edit = { render "Edit page" }
    def delete = { render "Delete page" }

    def hasRole = {}
    def lacksRole = {}
    def hasPermission = {}
}
