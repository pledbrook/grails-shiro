import org.apache.shiro.crypto.hash.Sha256Hash
import org.example.auth.*

class BootStrap {
    def init = { servletContext ->
        def adminRole = Role.findByName("Administrator")
        if (!adminRole) {
            adminRole = new Role(name: "Administrator").save(failOnError: true)
            def adminUser = new User(username: "admin", passwordHash: new Sha256Hash("admin").toHex()).save(failOnError: true)
            new UserRoleRel(user: adminUser, role: adminRole).save(failOnError: true)

            def normalUser = new User(username: "dilbert", passwordHash: new Sha256Hash("password").toHex()).save(failOnError: true)

            // Users for the TestController.
            def testUser1 = new User(username: "test1", passwordHash: new Sha256Hash("test1").toHex()).save(failOnError: true)
            def testUser2 = new User(username: "test2", passwordHash: new Sha256Hash("test2").toHex()).save(failOnError: true)

            // First user has access to index, show, create.
            def wildcardPermission = new Permission(type: "org.apache.shiro.authz.permission.WildcardPermission", possibleActions: "*").save(failOnError: true)
            new UserPermissionRel(user: testUser1, permission: wildcardPermission, target: "test:index,show,create", actions: "*").save(failOnError: true)

            // Second user has access to index, show, edit, delete.
            new UserPermissionRel(user: testUser2, permission: wildcardPermission, target: "test:index,show,edit,delete", actions: "*").save(failOnError: true)

            // Wildcard permissions.
            new UserPermissionRel(user: testUser1, permission: wildcardPermission, target: "w:index,one", actions: "*").save(failOnError: true)
            new UserPermissionRel(user: testUser2, permission: wildcardPermission, target: "w:index,two", actions: "*").save(failOnError: true)
            
            // Permissions required by the "test/index" view.
            new UserPermissionRel(user: testUser1, permission: wildcardPermission, target: "comments:view,edit", actions: "*").save(failOnError: true)
            new UserPermissionRel(user: testUser2, permission: wildcardPermission, target: "comments:view", actions: "*").save(failOnError: true)
            new UserPermissionRel(user: testUser2, permission: wildcardPermission, target: "tags", actions: "*").save(failOnError: true)

            // Some initial books that we can test against.
            new Book(title: "Colossus", author: "Niall Ferguson").save(failOnError: true)
            new Book(title: "Misery", author: "Stephen King").save(failOnError: true)
            new Book(title: "Guns, Germs, and Steel", author: "Jared Diamond").save(failOnError: true)
        }
    }

    def destroy = {
    }
} 
