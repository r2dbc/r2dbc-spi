/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.spi;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;

/**
 * Utility for discovering an available {@link ConnectionFactory} based on a set of {@link ConnectionFactoryOptions}.
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
        return AccessController.doPrivileged((PrivilegedAction<ServiceLoader<ConnectionFactoryProvider>>) () -> ServiceLoader.load(ConnectionFactoryProvider.class,
            ConnectionFactoryProvider.class.getClassLoader()));
    }

}
