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

package io.r2dbc.spi.test;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;

public final class MockConnectionFactory implements ConnectionFactory {

    private final Mono<Connection> connection;

    private MockConnectionFactory(@Nullable Mono<Connection> connection) {
        this.connection = connection;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MockConnectionFactory empty() {
        return builder().build();
    }

    @Override
    public Mono<Connection> create() {
        if (this.connection == null) {
            throw new AssertionError("Unexpected call to create()");
        }

        return this.connection;
    }

    @Override
    public MockConnectionFactoryMetadata getMetadata() {
        return MockConnectionFactoryMetadata.INSTANCE;
    }

    @Override
    public String toString() {
        return "MockConnectionFactory{" +
            "connection=" + this.connection +
            '}';
    }

    public static final class Builder {

        private Connection connection;

        private Builder() {
        }

        public MockConnectionFactory build() {
            return new MockConnectionFactory(this.connection == null ? null : Mono.just(this.connection));
        }

        public Builder connection(Connection connection) {
            this.connection = Assert.requireNonNull(connection, "connection must not be null");
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                "connection=" + this.connection +
                '}';
        }

    }

}
