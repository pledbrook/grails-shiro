// Place your Spring DSL code here
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher
import test.CustomRealm

beans = {
    credentialsMatcher(AllowAllCredentialsMatcher)

    customRealm(CustomRealm) {
        credentialsMatcher = ref('credentialsMatcher')
    }
}
