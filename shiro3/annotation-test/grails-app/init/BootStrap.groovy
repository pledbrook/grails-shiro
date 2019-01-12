import org.apache.shiro.crypto.hash.Sha256Hash

class BootStrap {

    def grailsApplication

    def init = { servletContext ->
        def adminRole = ShiroRole.findByName("Administrator")
        if (!adminRole) {
            adminRole = new ShiroRole(name: "Administrator")
            def adminUser = new ShiroUser(username: "admin", passwordHash: new Sha256Hash("admin").toHex())
            adminUser.addToRoles(adminRole)
            adminUser.save()

            def userRole = new ShiroRole(name: "User")
            def normalUser = new ShiroUser(username: "dilbert", passwordHash: new Sha256Hash("password").toHex())
            normalUser.addToRoles(userRole)
            normalUser.addToPermissions("book2:view")
            normalUser.save()

            // Users for the TestController.
            def testRole = new ShiroRole(name: "test")
            def testUser1 = new ShiroUser(username: "test1", passwordHash: new Sha256Hash("test1").toHex())
            testUser1.addToRoles(testRole)
            testUser1.addToPermissions("custom:read,write")
            testUser1.addToPermissions("book2:*")
            testUser1.save()

            // Some initial books that we can test against.
//            new Book(title: "Colossus", author: "Niall Ferguson").save()
//            new Book(title: "Misery", author: "Stephen King").save()
//            new Book(title: "Guns, Germs, and Steel", author: "Jared Diamond").save()
        }

        println grailsApplication.getMainContext().getBean('shiroFilter')?.dump()

    }
    def destroy = {
    }
}
