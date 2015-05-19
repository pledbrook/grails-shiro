package org.apache.shiro.authc.credential

import org.apache.shiro.crypto.hash.SimpleHash

/**
 * Bit of a hack: the sole purpose of this class is to work around the
 * problem that the HashedCredentialsMatcher class cannot be used directly
 * to encode passwords, for example for storage in the database. This
 * class also relies on the fact that Groovy ignores access level controls,
 * so it can call the protected <tt>HashedCredentialsMatcher.getCredentials()</tt>
 * method.
 */
class PasswordHashAdapter {
    CredentialsMatcher credentialMatcher

    /**
     * Hashes the given password. The username is only important if the
     * associated credentials matcher uses a salt for its hash algorithm.
     */
    String hash(CharSequence password, CharSequence username) {
        if (!(credentialMatcher instanceof HashedCredentialsMatcher)) {
            throw new IllegalStateException("Credential matcher must be of type HashedCredentialMatcher")
        }

        def hash = new SimpleHash(
                credentialMatcher.hashAlgorithmName,
                password.toString(),
                null,
                credentialMatcher.hashIterations)

        // We either need the password base64-encoded or hex-encoded
        // depending on how the credential matcher is configured.
        if (credentialMatcher.storedCredentialsHexEncoded) {
            return hash.toHex()
        }
        else {
            return hash.toBase64()
        }
    }
}
