/**
 * This was part of the former SecurityFilters which was split into many interceptors.
 */
class AuthInterceptor {

    //customize me
    int order = HIGHEST_PRECEDENCE + 200

    AuthInterceptor(){
        match(controller: /\b(?!(item|book|basic|form)\b)\w+/, action: "*")
    }
    boolean after() { 
          true
    }
    boolean before() {
        accessControl(auth: true) {
            true
        }
    }
}
