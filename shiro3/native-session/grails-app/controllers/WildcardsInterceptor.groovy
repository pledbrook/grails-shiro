/**
 * This was part of the former SecurityFilters which was split into many interceptors.
 */
class WildcardsInterceptor {

    //customize me
    int order = HIGHEST_PRECEDENCE - 4

    WildcardsInterceptor(){
        match(controller: "wildcard", action: "*")
    }
    boolean after() { 
          true
    }
    boolean before() {
        accessControl {
            permission("w:$actionName")
        }
    }
}
