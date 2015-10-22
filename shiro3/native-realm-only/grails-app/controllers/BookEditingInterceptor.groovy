/**
 * This was part of the former SecurityFilters which was split into many interceptors.
 */
class BookEditingInterceptor {

    //customize me
    int order = HIGHEST_PRECEDENCE + 200

    BookEditingInterceptor(){
        match(controller: "book", action: "(create|edit|save|update|delete)")
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
