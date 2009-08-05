class SecurityFilters {
    def filters = {
        auth(uri: "/**") {
            before = {
                accessControl { true }
            }
        }

        bookViewing(controller: "book", action: "(list|show)") {
            before = {
                accessControl {
                    permission("book:view")
                }
            }
        }

        bookEditing(controller: "book", action: "(create|edit|save|update|delete)") {
            before = {
                accessControl {
                    role("Administrator")
                }
            }
        }
    }
}
