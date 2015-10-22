import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class FormController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Form.list(params), model:[formCount: Form.count()]
    }

    def show(Form form) {
        respond form
    }

    def create() {
        respond new Form(params)
    }

    @Transactional
    def save(Form form) {
        if (form == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (form.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond form.errors, view:'create'
            return
        }

        form.save flush:true

        flash.message = message(code: 'default.created.message', args: [message(code: 'form.label', default: 'Form'), form.id])
        redirect form
    }

    def edit(Form form) {
        respond form
    }

    @Transactional
    def update(Form form) {
        if (form == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (form.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond form.errors, view:'edit'
            return
        }

        form.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'form.label', default: 'Form'), form.id])
                redirect form
            }
            '*'{ respond form, [status: OK] }
        }
    }

    @Transactional
    def delete(Form form) {

        if (form == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        form.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'form.label', default: 'Form'), form.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'form.label', default: 'Form'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
