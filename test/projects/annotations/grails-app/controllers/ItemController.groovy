class ItemController {
    def scaffold = true

    def myService

    def service = {
        render "Service returned: ${myService.testMethod()}"
    }
}
