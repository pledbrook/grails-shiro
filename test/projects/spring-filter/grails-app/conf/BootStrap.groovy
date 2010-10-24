import org.apache.shiro.crypto.hash.Sha256Hash

class BootStrap {
    def shiroSecurityService

    def init = { servletContext ->
        def adminRole = ShiroRole.findByName("Administrator")
        if (!adminRole) {
            adminRole = new ShiroRole(name: "Administrator").save()
            def adminUser = new ShiroUser(
                    username: "admin", 
                    passwordHash: shiroSecurityService.encodePassword("admin", "admin"))
            adminUser.addToRoles(adminRole)
            adminUser.save()

            def normalUser = new ShiroUser(
                    username: "dilbert",
                    passwordHash: shiroSecurityService.encodePassword("password", "dilbert")).save()

            // Users for the TestController.
            def testUser1 = new ShiroUser(
                    username: "test1",
                    passwordHash: shiroSecurityService.encodePassword("test1", "test1"))
            def testUser2 = new ShiroUser(
                    username: "test2",
                    passwordHash: shiroSecurityService.encodePassword("test2", "test2"))

            // First user has access to index, show, create.
            testUser1.addToPermissions("test:index,show,create")
            testUser1.addToPermissions("w:index,one")
            testUser1.addToPermissions("comments:view,edit")
            testUser1.save()

            // Second user has access to index, show, edit, delete.
            testUser2.addToPermissions("test:index,show,edit,delete")
            testUser1.addToPermissions("w:index,two")
            testUser2.addToPermissions("comments:view")
            testUser2.addToPermissions("tags")
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
