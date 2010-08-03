class UrlMappings {
    static mappings = {
        "/$controller/$action?/$id?"{
            constraints {
                // apply constraints here
            }
        }
        "500"(view:'/error')

        "/books" {
            controller = "book"
            action = "list"
        }
    }
}
