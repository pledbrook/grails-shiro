package annotation.test

import org.apache.shiro.authz.annotation.Logical
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.apache.shiro.authz.annotation.RequiresRoles

@RequiresAuthentication
@RequiresRoles(value=["User", "test"], logical=Logical.OR)
class Book2Controller {

    def index() {
        redirect(action: "list", params: params)
    }

    @RequiresPermissions('book2:list')
    def list(Integer max) {
        render(view: '/book2/simple', model: [msg: "list"])
    }

    @RequiresPermissions('book2:create')
    def create() {
        render(view: '/book2/simple', model: [msg: "create"])
    }

    @RequiresPermissions('book2:save')
    def save() {
        render(view: '/book2/simple', model: [msg: "save"])
    }

    @RequiresPermissions('book2:view')
    def show(Long id) {
        render(view: '/book2/simple', model: [msg: "show"])
    }

    @RequiresPermissions('book2:edit')
    def edit(Long id) {
        render(view: '/book2/simple', model: [msg: "edit"])
    }

    @RequiresPermissions('book2:update')
    def update(Long id, Long version) {
        render(view: '/book2/simple', model: [msg: "update"])
    }

    @RequiresPermissions('book2:delete')
    def delete(Long id) {
        render(view: '/book2/simple', model: [msg: "delete"])
    }
}
