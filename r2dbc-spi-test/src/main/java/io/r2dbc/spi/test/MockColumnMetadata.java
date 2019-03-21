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

import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.Nullability;

public final class MockColumnMetadata implements ColumnMetadata {

    private final Class<?> javaType;

    private final String name;

    private final Object nativeTypeMetadata;

    private final Nullability nullability;

    private final Integer precision;

    private final Integer scale;

    private MockColumnMetadata(@Nullable Class<?> javaType, String name, @Nullable Object nativeTypeMetadata, Nullability nullability, @Nullable Integer precision, @Nullable Integer scale) {
        this.javaType = javaType;
        this.name = Assert.requireNonNull(name, "name must not be null");
        this.nativeTypeMetadata = nativeTypeMetadata;
        this.nullability = Assert.requireNonNull(nullability, "nullability must not be null");
        this.precision = precision;
        this.scale = scale;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MockColumnMetadata empty() {
        return builder()
            .name("test-name")
            .nullability(Nullability.UNKNOWN)
            .build();
    }

    @Override
    public Class<?> getJavaType() {
        return this.javaType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeTypeMetadata() {
        return this.nativeTypeMetadata;
    }

    @Override
    public Nullability getNullability() {
        return this.nullability;
    }

    @Override
    public Integer getPrecision() {
        return this.precision;
    }

    @Override
    public Integer getScale() {
        return this.scale;
    }

    @Override
    public String toString() {
        return "MockColumnMetadata{" +
            "javaType=" + this.javaType +
            ", name='" + this.name + '\'' +
            ", nativeTypeMetadata=" + this.nativeTypeMetadata +
            ", nullability=" + this.nullability +
            ", precision=" + this.precision +
            ", scale=" + this.scale +
            '}';
    }

    public static final class Builder {

        private Class<?> javaType;

        private String name;

        private Object nativeTypeMetadata;

        private Nullability nullability = Nullability.UNKNOWN;

        private Integer precision;

        private Integer scale;

        private Builder() {
        }

        public MockColumnMetadata build() {
            return new MockColumnMetadata(this.javaType, this.name, this.nativeTypeMetadata, this.nullability, this.precision, this.scale);
        }

        public Builder javaType(Class<?> type) {
            this.javaType = Assert.requireNonNull(type, "javaType must not be null");
            return this;
        }

        public Builder name(String name) {
            this.name = Assert.requireNonNull(name, "name must not be null");
            return this;
        }

        public Builder nativeTypeMetadata(Object nativeTypeMetadata) {
            this.nativeTypeMetadata = Assert.requireNonNull(nativeTypeMetadata, "nativeTypeMetadata must not be null");
            return this;
        }

        public Builder nullability(Nullability nullability) {
            this.nullability = Assert.requireNonNull(nullability, "nullability must not be null");
            return this;
        }

        public Builder precision(Integer precision) {
            this.precision = Assert.requireNonNull(precision, "precision must not be null");
            return this;
        }

        public Builder scale(Integer precision) {
            this.scale = Assert.requireNonNull(precision, "scale must not be null");
            return this;
        }
    }

}
