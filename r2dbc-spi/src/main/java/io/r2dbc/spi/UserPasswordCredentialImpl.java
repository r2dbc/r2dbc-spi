package io.r2dbc.spi;

class UserPasswordCredentialImpl implements UserPasswordCredential {

    private String user;

    private CharSequence password;

    UserPasswordCredentialImpl(String user, CharSequence password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public String user() {
        return this.user;
    }

    @Override
    public CharSequence password() {
        return this.password;
    }
}
