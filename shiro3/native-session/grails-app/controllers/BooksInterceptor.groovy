/**
 * This was part of the former SecurityFilters which was split into many interceptors.
 */
class BooksInterceptor {

    //customize me
    int order = HIGHEST_PRECEDENCE - 2

    BooksInterceptor(){
        match(controller: "book", action: "*")
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
