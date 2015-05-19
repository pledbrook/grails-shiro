package org.apache.shiro.grails

class ShiroSecurityService {
    static transactional = false

    def passwordHashAdapter

    /**
     * Hashes a password based on the configured <tt>credentialMatcher</tt>
     * bean. If that bean does not implement <tt>HashedCredentialsMatcher</tt>
     * then this method won't work.
     * @param password The password to hash.
     * @param username The username of the user whose password this is.
     * This is only required if you are using salted hashes.
     */
    String encodePassword(CharSequence password, CharSequence username = "dummy") {
        return passwordHashAdapter.hash(password, username)
    }
}
