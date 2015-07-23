import org.apache.shiro.crypto.hash.Sha256Hash

class BootStrap {
    def init = { servletContext ->
        def adminRole = ShiroRole.findByName("Administrator")
        if (!adminRole) {
            adminRole = new ShiroRole(name: "Administrator")
            def adminUser = new ShiroUser(username: "admin", passwordHash: new Sha256Hash("admin").toHex())
            adminUser.addToRoles(adminRole)
            adminUser.save()

            def normalUser = new ShiroUser(username: "dilbert", passwordHash: new Sha256Hash("password").toHex()).save()

            // Users for the TestController.
            def testUser1 = new ShiroUser(username: "test1", passwordHash: new Sha256Hash("test1").toHex()).save()
            def testUser2 = new ShiroUser(username: "test2", passwordHash: new Sha256Hash("test2").toHex()).save()

            // First user has access to index, show, create.
            testUser1.addToPermissions("test:index,show,create")

            // Second user has access to index, show, edit, delete.
            testUser2.addToPermissions("test:index,show,edit,delete")

            // Wildcard permissions.
            testUser1.addToPermissions("w:index,one")
            testUser2.addToPermissions("w:index,two")
            
            // Permissions required by the "test/index" view.
            testUser1.addToPermissions("comments:view,edit")
            testUser2.addToPermissions("comments:view")
            testUser2.addToPermissions("tags")
            testUser1.save()
            testUser2.save()

            // Some initial books that we can test against.
            new Book(title: "Colossus", author: "Niall Ferguson").save()
            new Book(title: "Misery", author: "Stephen King").save()
            new Book(title: "Guns, Germs, and Steel", author: "Jared Diamond").save()
        }
    }

    def destroy = {
    }
} 
