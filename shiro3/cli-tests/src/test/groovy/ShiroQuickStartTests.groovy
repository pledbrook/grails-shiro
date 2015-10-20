import grails.test.AbstractCliTestCase
import org.apache.commons.codec.digest.DigestUtils

/**
 * Test case for the "generate-dto" Grails command provided by this
 * plugin.
 */
class ShiroQuickStartTests extends AbstractCliTestCase {
    private File realmFile
    private File userFile
    private File roleFile
    private File authFile
    private File interceptorFile
    private File viewFile
    private File realmsDir

    protected void setUp() {
        super.setUp()
        realmFile = new File(workDir, "grails-app/realms/ShiroDbRealm.groovy")
        userFile = new File(workDir, "grails-app/domain/ShiroUser.groovy")
        roleFile = new File(workDir, "grails-app/domain/ShiroRole.groovy")
        authFile = new File(workDir, "grails-app/controllers/AuthController.groovy")
        interceptorFile = new File(workDir, "grails-app/controllers/ShiroSecurityInterceptor.groovy")
        viewFile = new File(workDir, "grails-app/views/auth/login.gsp")
    
        realmsDir = new File(workDir, "grails-app/realms")

        userFile.delete()
        roleFile.delete()
        authFile.delete()
        interceptorFile.delete()
        viewFile.parentFile.deleteDir()
        realmsDir.deleteDir()
    }

    protected void tearDown() {
        super.tearDown()

        realmsDir.deleteDir()
        userFile.delete()
        roleFile.delete()
        authFile.delete()
        interceptorFile.delete()
        viewFile.parentFile.deleteDir()
    }

    /**
     * Tests that the shiro-quick-start command without any arguments creates
     * the wildcard realm, domain classes, auth controller and security filter
     * in the default package with a Shiro prefix.
     */
    void testNoArgs() {
        assert !realmFile.exists()
        assert !userFile.exists()
        assert !roleFile.exists()
        assert !authFile.exists()
        assert !interceptorFile.exists()
        assert !viewFile.exists()
        assert !realmsDir.exists()

        execute([ "shiro-quick-start" ])
        assertEquals 0, waitForProcess()
        //verifyHeader()

        // Make sure that the script was found.
        assertFalse "ShiroQuickStart script not found.", output.contains("Script not found:")

        // Check that the expected files are created.
        assertTrue "Realm was not created", realmFile.exists()
        assertTrue "User domain was not created", userFile.exists()
        assertTrue "Role domain was not created", roleFile.exists()
        assertTrue "Auth controller was not created", authFile.exists()
        assertTrue "Interceptor was not created", interceptorFile.exists()
        assertTrue "Login view was not created", viewFile.exists()
        
        def realmContent = realmFile.text.trim()
        assertFalse "Realm class is in a package", realmContent.startsWith("package")
        assertTrue  "Realm class name incorrect", realmContent.contains("class ShiroDbRealm {")
 
        def userContent = userFile.text.trim()
        assertFalse "User domain class is in a package", userContent.startsWith("package")
        assertTrue "User domain class name incorrect : $userContent", userContent.contains("class ShiroUser {")

        def roleContent = roleFile.text.trim()
        assertFalse "Role domain class is in a package", roleContent.startsWith("package")
        assertTrue "Role domain class name incorrect", roleContent.contains("class ShiroRole {")

        def authContent = authFile.text.trim()
        assertFalse "Auth controller class is in a package", authContent.startsWith("package")
        assertTrue "Auth controller class name incorrect", authContent.contains("class AuthController {")

        def interceptorContent = interceptorFile.text.trim()
        assertFalse "Interceptor class is in a package", interceptorContent.startsWith("package")
        assertTrue "Interceptor class name incorrect", interceptorContent.contains("class ShiroSecurityInterceptor {")

        def viewContent = viewFile.text.trim()
        assertTrue "Login view missing title", viewContent.contains("<title>Login</title>")

    }
}
