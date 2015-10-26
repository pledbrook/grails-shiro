class UrlMappings {
    static mappings = {
        "/$controller/$action?/$id?"{
            constraints {
                // apply constraints here
            }
        }

        "/auth/signIn"(controller: "auth", action: "signIn")
        "/login"(controller: "auth", action: "login")
        "/unauthorized"(controller: "auth", action: "unauthorized")
        "/"(view:"/index")
        "500"(view:'/error')

        "/books" {
            controller = "book"
            action = "list"
        }
    }
}
