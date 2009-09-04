import org.apache.shiro.crypto.hash.Sha1Hash

class BootStrap {
    def init = { servletContext ->
        // Normal user role.
        def userRole = new ShiroRole(name: "User")
        userRole.addToPermissions("book:list,show")

        // Normal user with the User role. 
        def normalUser = new ShiroUser(username: "dilbert", passwordHash: new Sha1Hash("password").toHex())
        normalUser.addToRoles(userRole)
        normalUser.addToPermissions("book:create")
        normalUser.save()

        // Users for the TestController.
        def testUser1 = new ShiroUser(username: "test1", passwordHash: new Sha1Hash("test1").toHex()).save()
        def testUser2 = new ShiroUser(username: "test2", passwordHash: new Sha1Hash("test2").toHex()).save()

        // Some initial books that we can test against.
        new Book(title: "Colossus", author: "Niall Ferguson").save()
        new Book(title: "Misery", author: "Stephen King").save()
        new Book(title: "Guns, Germs, and Steel", author: "Jared Diamond").save(flush: true)
    }

    def destroy = {
    }
} 
