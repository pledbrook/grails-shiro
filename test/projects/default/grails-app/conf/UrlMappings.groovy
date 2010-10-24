class UrlMappings {
    static mappings = {
        "/$controller/$action?/$id?"{
            constraints {
                // apply constraints here
            }
        }
        "/"(view: "index.gsp")
        "500"(view:'/error')

        "/books" {
            controller = "book"
            action = "list"
        }
    }
}
