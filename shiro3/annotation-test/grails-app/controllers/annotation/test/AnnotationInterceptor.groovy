/**
 * This was part of the former SecurityFilters which was split into many interceptors.
 */
class AnnotationInterceptor {

    //customize me
    int order = HIGHEST_PRECEDENCE + 200

    AnnotationInterceptor(){
        match(controller: '*', action: "*")
    }
    boolean after() { 
          true
    }
    boolean before() {
        true
    }
}
