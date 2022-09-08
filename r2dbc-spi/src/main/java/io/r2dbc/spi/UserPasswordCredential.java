package io.r2dbc.spi;

/**
 * UserPasswordCredentials used to authenticate with a database.
 */
public interface UserPasswordCredential extends Credential {

    /**
     * Returns the user of the {@link UserPasswordCredential}.
     *
     * @return the user of the {@link UserPasswordCredential}.
     */
    String user();

    /**
     * Returns the password of the {@link UserPasswordCredential}.
     *
     * @return the password of the {@link UserPasswordCredential}.
     */
    CharSequence password();
}
