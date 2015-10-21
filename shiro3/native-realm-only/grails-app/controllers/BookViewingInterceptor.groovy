/**
 * This was part of the former SecurityFilters which was split into many interceptors.
 */
class BookViewingInterceptor {

    //customize me
    int order = HIGHEST_PRECEDENCE + 200

    BookViewingInterceptor(){
        match(controller: "book", action: "(list|show)")
    }
    boolean after() { 
          true
    }
    boolean before() {
        accessControl {
            permission("book:view")
        }
    }
}
