package io.r2dbc.spi;

/**
 * UserPasswordCredentials used to authenticate with a database.
 */
public interface UserPasswordCredential extends Credential {
    String user();
    CharSequence password();
}
