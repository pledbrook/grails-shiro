class BookController {
    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']
    
    def index = { redirect(action:list,params:params) }

    def list = {
        if(!params.max) params.max = 10
        [ bookList: Book.list( params ) ]
    }

    def show = {
        def book = Book.get( params.id )

        if(!book) {
            flash.message = "Book not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ book : book ] }
    }

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

    def create = {
        def book = new Book()
        book.properties = params
        return ['book':book]
    }

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
