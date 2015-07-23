class UrlMappings {

    static mappings = {
        "/"(view:"/index")
        "/basic/$action/$id?"(controller: "basic")
        "/form/$action/$id?"(controller: "form")
        "/auth/signIn"(controller: "auth", action: "signIn")
        "/login"(controller: "auth", action: "login")
        "/unauthorized"(controller: "auth", action: "unauthorized")
        "500"(view:'/error')
    }
}
