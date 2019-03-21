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

import io.r2dbc.spi.Row;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MockRow implements Row {

    private final Map<Identified, Object> identified;

    private MockRow(Map<Identified, Object> identified) {
        this.identified = Assert.requireNonNull(identified, "identified must not be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MockRow empty() {
        return builder().build();
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(Object identifier, Class<T> type) {
        Assert.requireNonNull(identifier, "identifier must not be null");
        Assert.requireNonNull(type, "type must not be null");

        Identified identified = new Identified(identifier, type);

        if (!this.identified.containsKey(identified)) {
            throw new AssertionError(String.format("Unexpected call to get(Object, Class) with values '%s', '%s'", identifier, type.getName()));
        }

        return (T) this.identified.get(identified);
    }

    @Override
    public String toString() {
        return "MockRow{" +
            "identified=" + this.identified +
            '}';
    }

    public static final class Builder {

        private final Map<Identified, Object> identified = new HashMap<>();

        private Builder() {
        }

        public MockRow build() {
            return new MockRow(this.identified);
        }

        public Builder identified(Object identifier, Class<?> type, @Nullable Object value) {
            Assert.requireNonNull(identifier, "identifier must not be null");
            Assert.requireNonNull(type, "type must not be null");

            this.identified.put(new Identified(identifier, type), value);
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                "identified=" + this.identified +
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
