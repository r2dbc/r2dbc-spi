/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.spi;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.r2dbc.spi.ConnectionUrlParser.parseQuery;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link ConnectionUrlParser}.
 */
final class ConnectionUrlParserUnitTests {

    @Test
    void shouldRejectEmptyUrl() {
        assertThatThrownBy(() -> ConnectionUrlParser.validate(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConnectionUrlParser.validate("")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNonR2dbcUrl() {
        assertThatThrownBy(() -> ConnectionUrlParser.validate("foo://host")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConnectionUrlParser.validate("R2DBC:foo://")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectMalformedUrl() {
        assertThatThrownBy(() -> ConnectionUrlParser.validate("r2dbc://host")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConnectionUrlParser.validate("r2dbc:://host")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConnectionUrlParser.validate("r2dbc: ://host")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConnectionUrlParser.validate("r2dbc:host")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConnectionUrlParser.validate("r2dbc:/host")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldParseSingleHostUrl() {
        assertThat(parseQuery("r2dbc:foo://myhost")).hasDriver("foo").hasNotOption(ConnectionFactoryOptions.SSL).hasNoProtocol().hasHost("myhost").hasNoPort().hasNoDatabase().hasNotOption(ConnectionFactoryOptions.SSL);
    }

    @Test
    void shouldParseSecure() {
        assertThat(parseQuery("r2dbcs:foo://myhost")).hasDriver("foo").hasOption(ConnectionFactoryOptions.SSL).hasHost("myhost").hasNoPort().hasNoDatabase();
    }

    @Test
    void shouldParseSingleHostUrlWithProtocol() {
        assertThat(parseQuery("r2dbc:foo:bar://myhost")).hasDriver("foo").hasProtocol("bar").hasHost("myhost").hasNoPort().hasNoDatabase().hasNotOption(ConnectionFactoryOptions.SSL);
    }

    @Test
    void shouldParseSingleHostAndPort() {
        assertThat(parseQuery("r2dbc:foo://myhost:4711")).hasDriver("foo").hasNoProtocol().hasHost("myhost").hasPort(4711).hasNoUser().hasNoDatabase();
    }

    @Test
    void hasMultipleHosts() {
        assertThat(parseQuery("r2dbc:foo://host1,host2")).hasDriver("foo").hasNoProtocol().hasHost("host1,host2").hasNoPort().hasNoUser().hasNoDatabase();
        assertThat(parseQuery("r2dbc:foo://host1:123,host2:456")).hasDriver("foo").hasNoProtocol().hasHost("host1:123,host2:456").hasNoPort().hasNoUser().hasNoDatabase();
    }

    @Test
    void hasAuthentication() {
        assertThat(parseQuery("r2dbc:foo://user:password@myhost:4711")).hasDriver("foo").hasNoProtocol().hasHost("myhost").hasPort(4711).hasUser("user").hasNoDatabase();

        assertThat(parseQuery("r2dbc:foo://a%26b%26f%3Ac%3Dd:password%204%21@myhost:4711")).hasDriver("foo").hasNoProtocol().hasHost("myhost").hasPassword("password 4!").hasPort(4711).hasUser("a&b" +
            "&f:c=d").hasNoDatabase();
    }

    @Test
    void hasMultipleHostsWithAuthentication() {
        assertThat(parseQuery("r2dbc:foo://user:password@host1,host2")).hasDriver("foo").hasNoProtocol().hasHost("host1,host2").hasNoPort().hasUser("user").hasNoDatabase();
        assertThat(parseQuery("r2dbc:foo://user:password@host1:123,host2:456")).hasDriver("foo").hasNoProtocol().hasHost("host1:123,host2:456").hasNoPort().hasUser("user").hasNoDatabase();
    }

    @Test
    void hasDatabase() {
        assertThat(parseQuery("r2dbc:foo://myhost/")).hasDriver("foo").hasNoProtocol().hasHost("myhost").hasNoPort().hasNoDatabase();
        assertThat(parseQuery("r2dbc:foo://myhost/database")).hasDriver("foo").hasNoProtocol().hasHost("myhost").hasNoPort().hasDatabase("database");
        assertThat(parseQuery("r2dbc:foo://myhost/a%26b%26c%3Dd")).hasDriver("foo").hasNoProtocol().hasHost("myhost").hasNoPort().hasDatabase("a&b&c=d");
    }

    @Test
    void parsesQueryString() {
        assertThat(parseQuery("r2dbc:foo://myhost/database?foo=bar")).hasDriver("foo").hasNoProtocol().hasHost("myhost").hasNoPort().hasDatabase("database").hasOption("foo", "bar");
        assertThat(parseQuery("r2dbc:foo://myhost/database?foo=a%26b%26c%3Dd")).hasDriver("foo").hasNoProtocol().hasHost("myhost").hasNoPort().hasDatabase("database").hasOption("foo", "a&b&c=d");
    }

    @Test
    void rejectsWellKnownPropertiesInQueryString() {

        assertThatThrownBy(() -> parseQuery("r2dbc:foo://myhost/database?driver=foo")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> parseQuery("r2dbc:foo://myhost/database?protocol=foo")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> parseQuery("r2dbc:foo://myhost/database?user=foo")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> parseQuery("r2dbc:foo://myhost/database?password=foo")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> parseQuery("r2dbc:foo://myhost/database?host=foo")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> parseQuery("r2dbc:foo://myhost/database?port=foo")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> parseQuery("r2dbc:foo://myhost/database?database=foo")).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Create an assertion for a {@link ConnectionFactoryOptions}.
     *
     * @param actual
     */
    public static ConnectionFactoryOptionsAssert assertThat(ConnectionFactoryOptions actual) {
        return new ConnectionFactoryOptionsAssert(actual);
    }

    static class ConnectionFactoryOptionsAssert extends AbstractObjectAssert<ConnectionFactoryOptionsAssert, ConnectionFactoryOptions> {

        private ConnectionFactoryOptionsAssert(ConnectionFactoryOptions actual) {
            super(actual, ConnectionFactoryOptionsAssert.class);
        }


        ConnectionFactoryOptionsAssert hasOption(Option<?> option) {

            isNotNull();
            actual.getRequiredValue(option);

            return this;
        }

        ConnectionFactoryOptionsAssert hasNotOption(Option<?> option) {

            isNotNull();
            Assertions.assertThat(actual.hasOption(option)).describedAs(option.name()).isFalse();

            return this;
        }

        ConnectionFactoryOptionsAssert hasOption(String option, String value) {

            isNotNull();
            Assertions.assertThat(actual.getRequiredValue(Option.valueOf(option)).toString()).describedAs("Option " + option).isEqualTo(value);

            return this;
        }

        ConnectionFactoryOptionsAssert hasHost(String host) {

            Assertions.assertThat(actual.getRequiredValue(ConnectionFactoryOptions.HOST)).describedAs("Host").isEqualTo(host);

            return this;
        }

        ConnectionFactoryOptionsAssert hasNoPort() {

            hasNotOption(ConnectionFactoryOptions.PORT);

            return this;
        }

        ConnectionFactoryOptionsAssert hasPort() {

            hasOption(ConnectionFactoryOptions.PORT);

            return this;
        }

        ConnectionFactoryOptionsAssert hasPort(int port) {

            isNotNull();
            Assertions.assertThat(actual.getRequiredValue(ConnectionFactoryOptions.PORT)).describedAs("Port").isEqualTo(port);

            return this;
        }

        ConnectionFactoryOptionsAssert hasDriver(String driver) {

            isNotNull();
            Assertions.assertThat(actual.getRequiredValue(ConnectionFactoryOptions.DRIVER)).describedAs("Driver").isEqualTo(driver);

            return this;
        }

        ConnectionFactoryOptionsAssert hasNoProtocol() {

            hasNotOption(ConnectionFactoryOptions.PROTOCOL);

            return this;
        }

        ConnectionFactoryOptionsAssert hasProtocol() {

            hasOption(ConnectionFactoryOptions.PROTOCOL);

            return this;
        }

        ConnectionFactoryOptionsAssert hasProtocol(String protocol) {

            isNotNull();
            Assertions.assertThat(actual.getRequiredValue(ConnectionFactoryOptions.PROTOCOL)).describedAs("Protocol").isEqualTo(protocol);

            return this;
        }

        ConnectionFactoryOptionsAssert hasNoDatabase() {

            hasNotOption(ConnectionFactoryOptions.DATABASE);

            return this;
        }

        ConnectionFactoryOptionsAssert hasDatabase() {

            hasOption(ConnectionFactoryOptions.DATABASE);

            return this;
        }

        ConnectionFactoryOptionsAssert hasDatabase(String database) {

            isNotNull();
            Assertions.assertThat(actual.getRequiredValue(ConnectionFactoryOptions.DATABASE)).describedAs("Database").isEqualTo(database);

            return this;
        }

        ConnectionFactoryOptionsAssert hasNoUser() {

            hasNotOption(ConnectionFactoryOptions.USER);

            return this;
        }

        ConnectionFactoryOptionsAssert hasUser() {

            hasOption(ConnectionFactoryOptions.USER);

            return this;
        }

        ConnectionFactoryOptionsAssert hasUser(String user) {

            isNotNull();
            Assertions.assertThat(actual.getRequiredValue(ConnectionFactoryOptions.USER)).describedAs("User").isEqualTo(user);

            return this;
        }

        ConnectionFactoryOptionsAssert hasNoPassword() {

            isNotNull();
            hasNotOption(ConnectionFactoryOptions.PASSWORD);

            return this;
        }

        ConnectionFactoryOptionsAssert hasPassword() {

            isNotNull();
            hasOption(ConnectionFactoryOptions.PASSWORD);

            return this;
        }

        ConnectionFactoryOptionsAssert hasPassword(String password) {

            isNotNull();
            Assertions.assertThat(actual.getRequiredValue(ConnectionFactoryOptions.PASSWORD).toString()).describedAs("Password").isEqualTo(password);

            return this;
        }
    }
}
