/**
 * This was part of the former SecurityFilters which was split into many interceptors.
 */
class AuthInterceptor {

    //customize me
    int order = HIGHEST_PRECEDENCE + 200

    AuthInterceptor(){
        match(uri: "/**")
    }
    boolean after() { 
          true
    }
    boolean before() {
        if (!controllerName) return true
        accessControl { true }
    }
}
