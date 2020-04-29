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

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A holder for configuration options related to {@link ConnectionFactory}s.
 * <p>
 * A {@link ConnectionFactoryOptions} represents a configuration state that consists of one or more {@link Option} objects. Each configuration option can be specified at most once to associate a
 * value with its key.
 * <p>
 * New objects are constructed through {@link #builder()} or by {@link #parse(CharSequence) parsing} a R2DBC Connection URL.
 * {@link ConnectionFactoryOptions} can be augmented to allow staged construction of the configuration state using a {@link #mutate()
 * initialized builder} to create a new {@link ConnectionFactoryOptions} objects with mutations applied. Values of this class are immutable once created. Builder initializers are left unchanged.
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
 * </pre>
 *
 * @see ConnectionFactories
 */
public final class ConnectionFactoryOptions {

    /**
     * Connection timeout.
     */
    public static final Option<Duration> CONNECT_TIMEOUT = Option.valueOf("connectTimeout");

    /**
     * Initial database name.
     */
    public static final Option<String> DATABASE = Option.valueOf("database");

    /**
     * Driver name.
     */
    public static final Option<String> DRIVER = Option.valueOf("driver");

    /**
     * Endpoint host name.
     */
    public static final Option<String> HOST = Option.valueOf("host");

    /**
     * Password for authentication.
     */
    public static final Option<CharSequence> PASSWORD = Option.sensitiveValueOf("password");

    /**
     * Endpoint port number.
     */
    public static final Option<Integer> PORT = Option.valueOf("port");

    /**
     * Driver protocol name.  Typically represented as {@code tcp} or a database vendor-specific protocol string.
     */
    public static final Option<String> PROTOCOL = Option.valueOf("protocol");

    /**
     * Whether to require SSL.
     */
    public static final Option<Boolean> SSL = Option.valueOf("ssl");

    /**
     * User for authentication.
     */
    public static final Option<String> USER = Option.valueOf("user");

    private final Map<Option<?>, Object> options;

    private ConnectionFactoryOptions(Map<Option<?>, Object> options) {
        this.options = Assert.requireNonNull(options, "options must not be null");
    }

    /**
     * Returns a new {@link Builder}.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Parses a R2DBC Connection URL and returns the parsed {@link ConnectionFactoryOptions}.
     * URL format:
     * {@code r2dbc:driver[:protocol]}://[user:password@]host[:port][/path][?option=value]}.
     *
     * @param url the R2DBC URL.
     * @return the parsed {@link ConnectionFactoryOptions}.
     */
    public static ConnectionFactoryOptions parse(CharSequence url) {
        return ConnectionUrlParser.parseQuery(Assert.requireNonNull(url, "R2DBC Connection URL must not be null"));
    }

    /**
     * Returns a new {@link Builder} initialized with this {@link ConnectionFactoryOptions}.
     *
     * @return a new {@link Builder}
     */
    public Builder mutate() {
        return new Builder().from(this);
    }

    /**
     * Returns the value for an option if it exists.
     *
     * @param option the option to retrieve the value for
     * @param <T>    the type of the value
     * @return the value for an option
     * @throws IllegalArgumentException if {@code option} is {@code null}
     * @throws IllegalStateException    if there is no value for {@code option}
     */
    public <T> T getRequiredValue(Option<T> option) {
        T value = getValue(option);

        if (value != null) {
            return value;
        }

        throw new IllegalStateException(String.format("No value found for %s", option.name()));
    }

    /**
     * Returns the value for an option if it exists, otherwise {@code null}.
     *
     * @param option the option to retrieve the value for
     * @param <T>    the type of the value
     * @return the value for an option if it exists, otherwise {@code null}
     * @throws IllegalArgumentException if {@code option} is {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getValue(Option<T> option) {
        Assert.requireNonNull(option, "option must not be null");

        return (T) this.options.get(option);
    }

    /**
     * Returns {@code true} if the option exists, otherwise {@code false}.
     *
     * @param option the option to test for
     * @return {@code true} if the option exists, otherwise {@code false}
     * @throws IllegalArgumentException if {@code option} is {@code null}
     */
    public boolean hasOption(Option<?> option) {
        Assert.requireNonNull(option, "option must not be null");

        return this.options.containsKey(option);
    }

    @Override
    public String toString() {
        return "ConnectionFactoryOptions{" +
            "options=" + toString(this.options) +
            '}';
    }

    private static String toString(Map<Option<?>, Object> options) {
        List<String> o = new ArrayList<>(options.size());

        for (Map.Entry<Option<?>, Object> entry : options.entrySet()) {
            String key = entry.getKey().name();
            Object value = entry.getKey().sensitive() ? "REDACTED" : entry.getValue();
            o.add(String.format("%s=%s", key, value));
        }

        return String.format("{%s}", String.join(", ", o));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionFactoryOptions that = (ConnectionFactoryOptions) o;
        return options.equals(that.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(options);
    }

    /**
     * A builder for {@link ConnectionFactoryOptions} instances.
     * <p>
     * <i>This class is not threadsafe</i>
     */
    public static final class Builder {

        private final Map<Option<?>, Object> options = new HashMap<>();

        private Builder() {
        }

        /**
         * Returns a configured {@link ConnectionFactoryOptions}.
         *
         * @return a configured {@link ConnectionFactoryOptions}
         */
        public ConnectionFactoryOptions build() {
            return new ConnectionFactoryOptions(new HashMap<>(this.options));
        }

        /**
         * Populates the builder with the existing values from a configured {@link ConnectionFactoryOptions}.
         *
         * @param connectionFactoryOptions a configured {@link ConnectionFactoryOptions}
         * @return this {@link Builder}
         * @throws IllegalArgumentException if {@code connectionFactoryOptions} is {@code null}
         */
        public Builder from(ConnectionFactoryOptions connectionFactoryOptions) {
            return from(connectionFactoryOptions, it -> true);
        }

        /**
         * Populates the builder with the existing values from a configured {@link ConnectionFactoryOptions} allowing to {@link Predicate filter} which values to include.
         *
         * @param connectionFactoryOptions a configured {@link ConnectionFactoryOptions}
         * @param filter                   a {@link Predicate filter function} to include/exclude existing {@link Option}s.
         * @return this {@link Builder}
         * @throws IllegalArgumentException if {@code connectionFactoryOptions} or {@code filter} is {@code null}
         */
        public Builder from(ConnectionFactoryOptions connectionFactoryOptions, Predicate<Option<?>> filter) {
            Assert.requireNonNull(connectionFactoryOptions, "connectionFactoryOptions must not be null");
            Assert.requireNonNull(filter, "filter must not be null");

            connectionFactoryOptions.options.forEach((k, v) -> {
                if (filter.test(k)) {
                    this.options.put(k, v);
                }
            });

            return this;
        }

        /**
         * Configure an {@link Option} value.
         *
         * @param option the {@link Option} to configure
         * @param value  the {@link Option}'s value
         * @param <T>    the type of the value
         * @return this {@link Builder}
         * @throws IllegalArgumentException if {@code option} or {@code value} are {@code null}
         */
        public <T> Builder option(Option<T> option, T value) {
            Assert.requireNonNull(option, "option must not be null");
            Assert.requireNonNull(value, "value must not be null");

            this.options.put(option, value);
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                "options=" + ConnectionFactoryOptions.toString(this.options) +
                '}';
        }

    }

}
