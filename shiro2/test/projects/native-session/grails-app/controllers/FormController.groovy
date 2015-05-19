import org.springframework.dao.DataIntegrityViolationException

class FormController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [formInstanceList: Form.list(params), formInstanceTotal: Form.count()]
    }

    def create() {
        [formInstance: new Form(params)]
    }

    def save() {
        def formInstance = new Form(params)
        if (!formInstance.save(flush: true)) {
            render(view: "create", model: [formInstance: formInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'form.label', default: 'Form'), formInstance.id])
        redirect(action: "show", id: formInstance.id)
    }

    def show(Long id) {
        def formInstance = Form.get(id)
        if (!formInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'form.label', default: 'Form'), id])
            redirect(action: "list")
            return
        }

        [formInstance: formInstance]
    }

    def edit(Long id) {
        def formInstance = Form.get(id)
        if (!formInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'form.label', default: 'Form'), id])
            redirect(action: "list")
            return
        }

        [formInstance: formInstance]
    }

    def update(Long id, Long version) {
        def formInstance = Form.get(id)
        if (!formInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'form.label', default: 'Form'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (formInstance.version > version) {
                formInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'form.label', default: 'Form')] as Object[],
                          "Another user has updated this Form while you were editing")
                render(view: "edit", model: [formInstance: formInstance])
                return
            }
        }

        formInstance.properties = params

        if (!formInstance.save(flush: true)) {
            render(view: "edit", model: [formInstance: formInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'form.label', default: 'Form'), formInstance.id])
        redirect(action: "show", id: formInstance.id)
    }

    def delete(Long id) {
        def formInstance = Form.get(id)
        if (!formInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'form.label', default: 'Form'), id])
            redirect(action: "list")
            return
        }

        try {
            formInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'form.label', default: 'Form'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'form.label', default: 'Form'), id])
            redirect(action: "show", id: id)
        }
    }
}
