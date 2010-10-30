import org.example.*

class BootStrap {
    def shiroSecurityService

    def init = { servletContext ->
        // Administrator role has all permissions.
        def adminRole = new ShiroRole(name: "Administrator")
        adminRole.addToPermissions("*")
        adminRole.save()

        // An admin user.
        def adminUser = new ShiroUser(username: "admin", passwordHash: shiroSecurityService.encodePassword("admin")).save()
        adminUser.addToRoles(adminRole)
        adminUser.save()

        // Normal user role.
        def userRole = new ShiroRole(name: "User")
        userRole.addToPermissions("book:list,show")
        userRole.addToPermissions("test:*")
        userRole.save()

        // Normal user with the User role. 
        def normalUser = new ShiroUser(username: "dilbert", passwordHash: shiroSecurityService.encodePassword("password"))
        normalUser.addToRoles(userRole)
        normalUser.addToPermissions("book:create")
        normalUser.save()

        // Users for the TestController.
        def testUser1 = new ShiroUser(username: "test1", passwordHash: shiroSecurityService.encodePassword("test1")).save()
        def testUser2 = new ShiroUser(username: "test2", passwordHash: shiroSecurityService.encodePassword("test2")).save()

        // Extra permissions for the last two test users.
        testUser1.addToPermissions("test:index,show,create")
        testUser1.addToPermissions("w:index,one")
        testUser2.addToPermissions("test:index,show,edit,delete")
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
        new Book(title: "Guns, Germs, and Steel", author: "Jared Diamond").save(flush: true)
    }

    def destroy = {
    }
} 
