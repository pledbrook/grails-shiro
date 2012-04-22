package org.apache.shiro.grails

import org.apache.shiro.authz.annotation.*
import org.apache.shiro.authz.aop.*
import org.apache.shiro.grails.annotations.*
import org.springframework.beans.factory.InitializingBean

class ShiroAnnotationHandlerService implements InitializingBean {
    static transactional = false

    def grailsApplication

    protected authcHandlers
    protected authzHandlers
    protected handlerMap

    /**
     * Loads all the controller classes in the application and scans them for
     * Shiro annotations.
     */
    void afterPropertiesSet() {
        authcHandlers = [
                (RequiresGuest): new GuestAnnotationHandler(),
                (RequiresUser): new UserAnnotationHandler(),
                (RequiresAuthentication): new AuthenticatedAnnotationHandler() ]
        authzHandlers = [
                (RequiresRoles): new RoleAnnotationHandler(),
                (RequiresPermissions): new PermissionAnnotationHandler(),
                (RoleRequired): new LegacyRoleAnnotationHandler(),
                (PermissionRequired): new LegacyPermissionAnnotationHandler() ]
        handlerMap = [:]

        grailsApplication.controllerClasses.each { controller ->
            processController(controller)
        }
    }

    def getHandlersFor(String controller, String action) {
        def handlers = handlerMap["${controller}:${action}"]
        return handlers ?: []
    }


    protected void processController(controllerClass) {
        def authcAnnotations = authcHandlers.keySet()
        def authzAnnotations = authzHandlers.keySet()

        // We start by checking out the annotations on the class. These
        // can be overridden by the action (field) annotations.
        def authcClassHandler = null
        def authzClassHandler = null
        def clazz = controllerClass.clazz
        clazz.declaredAnnotations.each { ann ->
            def annClass = ann.annotationType()
            if (annClass in authcAnnotations) {
                authcClassHandler = [ ann, authcHandlers[annClass] ]
            }
            else if (annClass in authzAnnotations) {
                authzClassHandler = [ ann, authzHandlers[annClass] ]
            }
        }

        // Next, we go through the controller's fields
        clazz.declaredFields.each { field ->
            // At the start, this field uses the class annotation handlers.
            def authcHandler = authcClassHandler
            def authzHandler = authzClassHandler

            // Go through the field's annotations and check whether any are
            // Shiro requirements.
            field.declaredAnnotations.each { ann ->
                def annClass = ann.annotationType()
                if (annClass in authcAnnotations) {
                    authcHandler = [ ann, authcHandlers[annClass] ]
                }
                else if (annClass in authzAnnotations) {
                    authzHandler = [ ann, authzHandlers[annClass] ]
                }
            }

            // Create combined annotations + handlers where required and add
            // them to the handler map.
            def handlerKey = controllerClass.logicalPropertyName + ":" + field.name
            handlerMap[handlerKey] = []

            if (authcHandler) {
                // Neat trick: we spread the annotation/handler pairs to form
                // the arguments for the annotation handler.
                handlerMap[handlerKey] << new AnnotationHandler(*authcHandler)
            }

            if (authzHandler) {
                handlerMap[handlerKey] << new AnnotationHandler(*authzHandler)
            }
        }
    }
}
