class SecurityFilters {
    def filters = {
        auth(controller: /\b(?!(item|book)\b)\w+/, action: "*") {
            before = {
                accessControl(auth: true) {
                    true
                }
            }
        }

        items(controller: "itemSecured", action: "*") {
            before = {
                accessControl {
                    role("Administrator")
                }
            }
        }

        books(controller: "book", action: "*") {
            before = {
                accessControl {
                    permission("book:$actionName")
                }
            }
        }

        test(controller: "test", action: "(create|edit|save|update|delete)") {
            before = {
                accessControl()
            }
        }

        wildcards(controller: "wildcard", action: "*") {
            before = {
                accessControl {
                    permission("w:$actionName")
                }
            }
        }
    }
}
