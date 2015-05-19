package annotation.test

import org.apache.shiro.authz.annotation.Logical
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.apache.shiro.authz.annotation.RequiresRoles

@RequiresAuthentication
@RequiresRoles(value=["User", "test"], logical=Logical.OR)
class BookController {

    def index() {
        redirect(action: "list", params: params)
    }

    @RequiresPermissions('book:list')
    def list(Integer max) {
        render(view: '/book/simple', model: [msg: "list"])
    }

    @RequiresPermissions('book:create')
    def create() {
        render(view: '/book/simple', model: [msg: "create"])
    }

    @RequiresPermissions('book:save')
    def save() {
        render(view: '/book/simple', model: [msg: "save"])
    }

    @RequiresPermissions('book:view')
    def show(Long id) {
        render(view: '/book/simple', model: [msg: "show"])
    }

    @RequiresPermissions('book:edit')
    def edit(Long id) {
        render(view: '/book/simple', model: [msg: "edit"])
    }

    @RequiresPermissions('book:update')
    def update(Long id, Long version) {
        render(view: '/book/simple', model: [msg: "update"])
    }

    @RequiresPermissions('book:delete')
    def delete(Long id) {
        render(view: '/book/simple', model: [msg: "delete"])
    }
}
