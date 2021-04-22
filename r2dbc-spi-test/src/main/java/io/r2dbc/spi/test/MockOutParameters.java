/*
 * Copyright 2017-2021 the original author or authors.
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

import io.r2dbc.spi.OutParameters;
import io.r2dbc.spi.OutParametersMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MockOutParameters implements OutParameters {

    private final Map<Identified, Object> identified;

    private final OutParametersMetadata metadata;

    private MockOutParameters(Map<Identified, Object> identified, OutParametersMetadata metadata) {
        this.identified = Assert.requireNonNull(identified, "identified must not be null");
        this.metadata = Assert.requireNonNull(metadata, "metadata must not be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MockOutParameters empty() {
        return builder().build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(int index, Class<T> type) {

        Assert.requireNonNull(type, "type must not be null");

        Identified identified = new Identified(index, type);

        if (!this.identified.containsKey(identified)) {
            throw new AssertionError(String.format("Unexpected call to get(Object, Class) with values '%s', '%s'", index, type.getName()));
        }

        return (T) this.identified.get(identified);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String name, Class<T> type) {

        Assert.requireNonNull(name, "name must not be null");
        Assert.requireNonNull(type, "type must not be null");

        Identified identified = new Identified(name, type);

        if (!this.identified.containsKey(identified)) {
            throw new AssertionError(String.format("Unexpected call to get(Object, Class) with values '%s', '%s'", name, type.getName()));
        }

        return (T) this.identified.get(identified);
    }

    @Override
    public OutParametersMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public String toString() {
        return "MockOutParameters{" +
            "identified=" + this.identified +
            '}';
    }

    public static final class Builder {

        private final Map<Identified, Object> identified = new HashMap<>();

        private OutParametersMetadata metadata = MockOutParametersMetadata.empty();

        private Builder() {
        }

        public MockOutParameters build() {
            return new MockOutParameters(this.identified, this.metadata);
        }

        public Builder identified(Object identifier, Class<?> type, @Nullable Object value) {
            Assert.requireNonNull(identifier, "identifier must not be null");
            Assert.requireNonNull(type, "type must not be null");

            this.identified.put(new Identified(identifier, type), value);
            return this;
        }

        public Builder metadata(OutParametersMetadata outParametersMetadata) {
            this.metadata = Assert.requireNonNull(outParametersMetadata, "outParametersMetadata must not be null");
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                "identified=" + this.identified +
                "outParametersMetadata=" + this.metadata +
                '}';
        }

    }

    private static final class Identified {

        private final Object identifier;

        private final Class<?> type;

        private Identified(Object identifier, Class<?> type) {
            this.identifier = Assert.requireNonNull(identifier, "identifier must not be null");
            this.type = Assert.requireNonNull(type, "type must not be null");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Identified that = (Identified) o;
            return Objects.equals(this.identifier, that.identifier) &&
                Objects.equals(this.type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.identifier, this.type);
        }

        @Override
        public String toString() {
            return "Identified{" +
                "identifier=" + this.identifier +
                ", type=" + this.type +
                '}';
        }

    }

}
