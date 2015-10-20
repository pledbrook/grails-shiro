/**
 * This was part of the former SecurityFilters which was split into many interceptors.
 */
class ItemsInterceptor {

    //customize me
    int order = HIGHEST_PRECEDENCE + 200

    ItemsInterceptor(){
        match(controller: "itemSecured", action: "*")
    }
    boolean after() { 
          true
    }
    boolean before() {
        accessControl {
            role("Administrator")
        }
    }
}
