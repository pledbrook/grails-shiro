/**
 * This was part of the former SecurityFilters which was split into many interceptors.
 */
class TestInterceptor {

    //customize me
    int order = HIGHEST_PRECEDENCE - 3

    TestInterceptor(){
        match(controller: "test", action: "(create|edit|save|update|delete)")
    }
    boolean after() { 
          true
    }
    boolean before() {
        accessControl()
    }
}
