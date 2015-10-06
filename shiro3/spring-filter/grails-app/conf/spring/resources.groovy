import org.apache.shiro.authc.credential.Md5CredentialsMatcher

// Place your Spring DSL code here
beans = {
    credentialMatcher(Md5CredentialsMatcher) {
        storedCredentialsHexEncoded = false
        hashSalted = true
    }
}
