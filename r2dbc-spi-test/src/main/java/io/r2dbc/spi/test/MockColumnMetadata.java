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

package io.r2dbc.spi.test;

import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.Nullability;

public final class MockColumnMetadata implements ColumnMetadata {

    public static final String EMPTY_NAME = "empty-name";

    public static final Nullability EMPTY_NULLABILITY = Nullability.UNKNOWN;

    private final String name;

    private final Integer precision;

    private final Integer scale;

    private final Nullability nullability;

    private final Class<?> type;

    private final Object nativeMetadata;

    private MockColumnMetadata(String name, @Nullable Integer precision, @Nullable Integer scale, Nullability nullability, @Nullable Class<?> type, @Nullable Object nativeMetadata) {
        this.name = Assert.requireNonNull(name, "name must not be null");
        this.precision = precision;
        this.scale = scale;
        this.nullability = Assert.requireNonNull(nullability, "nullability must not be null");
        this.type = type;
        this.nativeMetadata = nativeMetadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MockColumnMetadata empty() {
        return builder()
            .name(EMPTY_NAME)
            .nullability(EMPTY_NULLABILITY)
            .build();
    }

    @Override
    public String getName() {
        return this.name;
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
    public Nullability getNullability() {
        return this.nullability;
    }

    @Override
    public Class<?> getJavaType() {
        return this.type;
    }

    @Override
    public Object getNativeTypeMetadata() {
        return this.nativeMetadata;
    }

    @Override
    public String toString() {
        return "MockColumnMetadata{" +
            "name='" + this.name + '\'' +
            ", precision=" + this.precision +
            ", scale=" + this.scale +
            ", nullability=" + this.nullability +
            ", type=" + this.type +
            ", nativeMetadata=" + this.nativeMetadata +
            '}';
    }

    public static final class Builder {

        private String name;

        private Integer precision;

        private Integer scale;

        private Nullability nullability;

        private Class<?> type;

        private Object nativeMetadata;

        private Builder() {
        }

        public MockColumnMetadata build() {
            return new MockColumnMetadata(this.name, this.precision, this.scale, this.nullability, this.type, this.nativeMetadata);
        }

        public Builder name(String name) {
            this.name = Assert.requireNonNull(name, "name must not be null");
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

        public Builder nullability(Nullability nullability) {
            this.nullability = Assert.requireNonNull(nullability, "nullability must not be null");
            return this;
        }

        public Builder type(Class<?> type) {
            this.type = Assert.requireNonNull(type, "type must not be null");
            return this;
        }

        public Builder nativeMetadata(Object nativeMetadata) {
            this.nativeMetadata = Assert.requireNonNull(nativeMetadata, "nativeMetadata must not be null");
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                "name='" + this.name + '\'' +
                ", precision=" + this.precision +
                ", scale=" + this.scale +
                ", nullability=" + this.nullability +
                ", type=" + this.type +
                ", nativeMetadata=" + this.nativeMetadata +
                '}';
        }
    }

}
