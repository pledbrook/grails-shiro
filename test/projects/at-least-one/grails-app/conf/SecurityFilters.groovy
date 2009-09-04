class SecurityFilters {
    def filters = {
        auth(controller: "*", action: "*") {
            before = {
                accessControl(auth: true)
            }
        }
    }
}
