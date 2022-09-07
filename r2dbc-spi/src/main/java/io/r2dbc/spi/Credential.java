package io.r2dbc.spi;

/**
 * Credentials used to authenticate with a database. Credential objects are
 * mutable. Mutability allows any security sensitive values retained by a
 * {@code Credential} to be cleared from memory. Drivers MUST NOT retain a
 * reference to a {@code Credential} object after consuming it for database
 * authentication.
 */
interface Credential {
}