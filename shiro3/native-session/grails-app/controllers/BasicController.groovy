import org.springframework.dao.DataIntegrityViolationException

class BasicController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [basicInstanceList: Basic.list(params), basicInstanceTotal: Basic.count()]
    }

    def create() {
        [basicInstance: new Basic(params)]
    }

    def save() {
        def basicInstance = new Basic(params)
        if (!basicInstance.save(flush: true)) {
            render(view: "create", model: [basicInstance: basicInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'basic.label', default: 'Basic'), basicInstance.id])
        redirect(action: "show", id: basicInstance.id)
    }

    def show(Long id) {
        def basicInstance = Basic.get(id)
        if (!basicInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'basic.label', default: 'Basic'), id])
            redirect(action: "list")
            return
        }

        [basicInstance: basicInstance]
    }

    def edit(Long id) {
        def basicInstance = Basic.get(id)
        if (!basicInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'basic.label', default: 'Basic'), id])
            redirect(action: "list")
            return
        }

        [basicInstance: basicInstance]
    }

    def update(Long id, Long version) {
        def basicInstance = Basic.get(id)
        if (!basicInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'basic.label', default: 'Basic'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (basicInstance.version > version) {
                basicInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'basic.label', default: 'Basic')] as Object[],
                          "Another user has updated this Basic while you were editing")
                render(view: "edit", model: [basicInstance: basicInstance])
                return
            }
        }

        basicInstance.properties = params

        if (!basicInstance.save(flush: true)) {
            render(view: "edit", model: [basicInstance: basicInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'basic.label', default: 'Basic'), basicInstance.id])
        redirect(action: "show", id: basicInstance.id)
    }

    def delete(Long id) {
        def basicInstance = Basic.get(id)
        if (!basicInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'basic.label', default: 'Basic'), id])
            redirect(action: "list")
            return
        }

        try {
            basicInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'basic.label', default: 'Basic'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'basic.label', default: 'Basic'), id])
            redirect(action: "show", id: id)
        }
    }
}
