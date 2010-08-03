import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.apache.shiro.authz.annotation.RequiresUser

@RequiresUser
class BookController {
    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']
    
    def index = { redirect(action:list,params:params) }

    def unsecured = {
        render "Unsecured page"
    }

    @RequiresPermissions("book:view")
    def list = {
        if(!params.max) params.max = 10
        [ bookList: Book.list( params ) ]
    }

    @RequiresPermissions("book:view")
    def show = {
        def book = Book.get( params.id )

        if(!book) {
            flash.message = "Book not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ book : book ] }
    }

    @RequiresPermissions("book:delete")
    def delete = {
        def book = Book.get( params.id )
        if(book) {
            book.delete()
            flash.message = "Book ${params.id} deleted"
            redirect(action:list)
        }
        else {
            flash.message = "Book not found with id ${params.id}"
            redirect(action:list)
        }
    }

    @RequiresPermissions("book:edit")
    def edit = {
        def book = Book.get( params.id )

        if(!book) {
            flash.message = "Book not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ book : book ]
        }
    }

    @RequiresPermissions("book:edit")
    def update = {
        def book = Book.get( params.id )
        if(book) {
            book.properties = params
            if(!book.hasErrors() && book.save()) {
                flash.message = "Book ${params.id} updated"
                redirect(action:show,id:book.id)
            }
            else {
                render(view:'edit',model:[book:book])
            }
        }
        else {
            flash.message = "Book not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    @RequiresAuthentication
    @RequiresPermissions("book:create")
    def create = {
        def book = new Book()
        book.properties = params
        return ['book':book]
    }

    @RequiresPermissions("book:create")
    def save = {
        def book = new Book(params)
        if(!book.hasErrors() && book.save()) {
            flash.message = "Book ${book.id} created"
            redirect(action:show,id:book.id)
        }
        else {
            render(view:'create',model:[book:book])
        }
    }
}
