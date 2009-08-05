import org.apache.shiro.crypto.hash.Sha1Hash

class BootStrap {
    def init = { servletContext ->
        def adminRole = new Role(name: "Administrator").save()
        def adminUser = new User(username: "admin", passwordHash: new Sha1Hash("admin").toHex()).save()
        new UserRoleRel(user: adminUser, role: adminRole).save()

        def normalUser = new User(username: "dilbert", passwordHash: new Sha1Hash("password").toHex()).save()

        // Users for the TestController.
        def testUser1 = new User(username: "test1", passwordHash: new Sha1Hash("test1").toHex()).save()
        def testUser2 = new User(username: "test2", passwordHash: new Sha1Hash("test2").toHex()).save()

        // First user has access to index, show, create.
        def permission = new Permission(type: "org.apache.shiro.grails.ShiroBasicPermission", possibleActions: "*").save()
        new UserPermissionRel(user: testUser1, permission: permission, target: "test", actions: "index,show,create").save()

        // Second user has access to index, show, edit, delete.
        new UserPermissionRel(user: testUser2, permission: permission, target: "test", actions: "index,show,edit,delete").save()

        // Wildcard permissions.
        def wildcardPermission = new Permission(type: "org.apache.shiro.authz.permission.WildcardPermission", possibleActions: "*").save()
        new UserPermissionRel(user: testUser1, permission: wildcardPermission, target: "w:index,one", actions: "*").save()
        new UserPermissionRel(user: testUser2, permission: wildcardPermission, target: "w:index,two", actions: "*").save()
        
        // Permissions required by the "test/index" view.
        new UserPermissionRel(user: testUser1, permission: wildcardPermission, target: "comments:view,edit", actions: "*").save()
        new UserPermissionRel(user: testUser2, permission: wildcardPermission, target: "comments:view", actions: "*").save()
        new UserPermissionRel(user: testUser2, permission: wildcardPermission, target: "tags", actions: "*").save()

        // Some initial books that we can test against.
        new Book(title: "Colossus", author: "Niall Ferguson").save()
        new Book(title: "Misery", author: "Stephen King").save()
        new Book(title: "Guns, Germs, and Steel", author: "Jared Diamond").save()
    }

    def destroy = {
    }
} 
