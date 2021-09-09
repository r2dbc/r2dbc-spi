/*
 * Copyright 2017-2019 the original author or authors.
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

import java.util.ServiceLoader;

/**
 * Utility for discovering an available {@link ConnectionFactory} based on a set of {@link ConnectionFactoryOptions}.
 * <p>
 * This utility provides runtime discovery of R2DBC implementations that are available through the class path. A {@link ConnectionFactory} can be obtained in two ways:
 *
 * <ul>
 *     <li>By using R2DBC Connection URL with {@link #get(String)}</li>
 *     <li>Programmatically using {@link #get(ConnectionFactoryOptions)}</li>
 * </ul>
 *
 * <h3>R2DBC Connection URL</h3>
 * R2DBC defines a standard URL format that is an enhanced form of <a href="https://www.ietf.org/rfc/rfc3986.txt" target="_blank">RFC 3986</a>. The following example shows a valid R2DBC URL:
 *
 * <pre class="code">
 * r2dbc:a-driver:pipes://localhost:3306/my_database?locale=en_US
 * \___/ \______/ \___/   \____________/\__________/\___________/
 *   |       |      |           |           |           |
 * scheme  driver  protocol  authority    path        query
 * </pre>
 *
 * <ul>
 *     <li>
 *        {@code scheme}: Identify that the URL is a valid R2DBC URL. Valid schemes are {@code r2dbc} and {@code r2dbcs} (configure SSL usage).
 *     </li>
 *     <li>
 *        {@code driver}: Identifier for a driver. The specification has no authority over driver identifiers.
 *     </li>
 *     <li>
 *        {@code protocol}: Used as optional protocol information to configure a driver-specific protocol. Protocols can be organized hierarchically and are separated by a colon (:).
 *     </li>
 *     <li>
 *        {@code authority}: Contains an endpoint and authorization. The authority may contain a single host or a collection of hostnames and port tuples by separating these with a comma (,).
 *     </li>
 *     <li>
 *         {@code path}: (optional) Used as an initial schema or database name.
 *     </li>
 *     <li>
 *         {@code query}: (optional) Used to pass additional configuration options in the form of String key-value pairs by using the key name as the option name.
 *     </li>
 *     <li>
 *         {@code fragment}: Unused (reserved for future use).
 *     </li>
 * </ul>
 * <p>
 * Example usage:
 * <pre class="code">
 * ConnectionFactory factory = ConnectionFactories.get("r2dbc:a-driver:pipes://localhost:3306/my_database?locale=en_US");
 * </pre>
 *
 * <h3>Programmatic {@link ConnectionFactory} Lookup</h3>
 * A {@link ConnectionFactory} can be requested by providing a {@link ConnectionFactoryOptions} object to {@link ConnectionFactories#get(ConnectionFactoryOptions)}. {@link ConnectionFactoryOptions}
 * can be built using a builder from {@link ConnectionFactoryOptions} tuples. Once created, {@link ConnectionFactoryOptions} is immutable.
 * <p>
 * Example usage:
 * <pre class="code">
 * ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
 *     .option(ConnectionFactoryOptions.DRIVER, "a-driver")
 *     .option(ConnectionFactoryOptions.PROTOCOL, "pipes")
 *     .option(ConnectionFactoryOptions.HOST, "localhost")
 *     .option(ConnectionFactoryOptions.PORT, 3306)
 *     .option(ConnectionFactoryOptions.DATABASE, "my_database")
 *     .option(Option.valueOf("locale"), "en_US")
 *     .build();
 *
 * ConnectionFactory factory = ConnectionFactories.get(options);
 * </pre>
 *
 * @see ConnectionFactoryOptions
 * @see ConnectionFactoryOptions#parse(CharSequence)
 * @see ServiceLoader
 */
public final class ConnectionFactories {

    private ConnectionFactories() {
    }

    /**
     * Returns a {@link ConnectionFactory} if an available implementation can be created from a collection of {@link ConnectionFactoryOptions}.
     *
     * @param connectionFactoryOptions a collection of {@link ConnectionFactoryOptions}
     * @return the created {@link ConnectionFactory} if one can be created, otherwise {@code null}
     * @throws IllegalArgumentException if {@code connectionSpecification} is {@code null}
     */
    @Nullable
    public static ConnectionFactory find(ConnectionFactoryOptions connectionFactoryOptions) {
        Assert.requireNonNull(connectionFactoryOptions, "connectionFactoryOptions must not be null");

        for (ConnectionFactoryProvider provider : loadProviders()) {
            if (provider.supports(connectionFactoryOptions)) {
                return provider.create(connectionFactoryOptions);
            }
        }

        return null;
    }

    /**
     * Returns a {@link ConnectionFactory} from an available implementation, created from a R2DBC Connection URL.
     * R2DBC URL format is:
     * {@code r2dbc:driver[:protocol]}://[user:password@]host[:port][/path][?option=value]}.
     *
     * @param url the R2DBC connection url
     * @return the created {@link ConnectionFactory}
     * @throws IllegalArgumentException if {@code url} is {@code null}
     * @throws IllegalStateException    if no available implementation can create a {@link ConnectionFactory}
     */
    public static ConnectionFactory get(String url) {
        return get(ConnectionFactoryOptions.parse(Assert.requireNonNull(url, "R2DBC Connection URL must not be null")));
    }

    /**
     * Returns a {@link ConnectionFactory} from an available implementation, created from a collection of {@link ConnectionFactoryOptions}.
     *
     * @param connectionFactoryOptions a collection of {@link ConnectionFactoryOptions}
     * @return the created {@link ConnectionFactory}
     * @throws IllegalArgumentException if {@code connectionFactoryOptions} is {@code null}
     * @throws IllegalStateException    if no available implementation can create a {@link ConnectionFactory}
     */
    public static ConnectionFactory get(ConnectionFactoryOptions connectionFactoryOptions) {
        ConnectionFactory connectionFactory = find(connectionFactoryOptions);

        if (connectionFactory == null) {
            throw new IllegalStateException(String.format("Unable to create a ConnectionFactory for '%s'. Available drivers: [ %s ]", connectionFactoryOptions, getAvailableDrivers()));
        }

        return connectionFactory;
    }

    /**
     * Returns whether a {@link ConnectionFactory} can be created from a collection of {@link ConnectionFactoryOptions}.
     *
     * @param connectionFactoryOptions a collection of {@link ConnectionFactoryOptions}
     * @return {@code true} if a {@link ConnectionFactory} can be created from a collection of {@link ConnectionFactoryOptions}, {@code false} otherwise.
     * @throws IllegalArgumentException if {@code connectionFactoryOptions} is {@code null}
     */
    public static boolean supports(ConnectionFactoryOptions connectionFactoryOptions) {
        Assert.requireNonNull(connectionFactoryOptions, "connectionFactoryOptions must not be null");

        for (ConnectionFactoryProvider provider : loadProviders()) {
            if (provider.supports(connectionFactoryOptions)) {
                return true;
            }
        }

        return false;
    }

    private static String getAvailableDrivers() {
        StringBuilder availableDrivers = new StringBuilder();

        for (ConnectionFactoryProvider provider : loadProviders()) {
            if (availableDrivers.length() != 0) {
                availableDrivers.append(", ");
            }
            availableDrivers.append(provider.getDriver());
        }

        if (availableDrivers.length() == 0) {
            availableDrivers.append("None");
        }

        return availableDrivers.toString();
    }

    private static ServiceLoader<ConnectionFactoryProvider> loadProviders() {
        return ServiceLoader.load(ConnectionFactoryProvider.class, ConnectionFactoryProvider.class.getClassLoader());
    }

}
