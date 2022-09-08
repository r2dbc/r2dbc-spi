package io.r2dbc.spi;

/** Factory for creating {@link Credential} objects */
public final class CredentialFactory {

    private CredentialFactory() { }

    /**
     * Returns a new {@link UserPasswordCredential}
     * @param user Username. Not null.
     * @param password Password. Not null.
     */
    public static UserPasswordCredential createUserPasswordCredential(
            String user, CharSequence password) {
        return new UserPasswordCredentialImpl(user, password);
    }

}
