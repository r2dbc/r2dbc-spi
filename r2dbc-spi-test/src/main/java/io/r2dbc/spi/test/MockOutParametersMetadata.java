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

import io.r2dbc.spi.OutParameterMetadata;
import io.r2dbc.spi.OutParametersMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class MockOutParametersMetadata implements OutParametersMetadata {

    private final List<OutParameterMetadata> outParameterMetadatas;

    private MockOutParametersMetadata(List<OutParameterMetadata> outParameterMetadatas) {
        this.outParameterMetadatas = Assert.requireNonNull(outParameterMetadatas, "outParameterMetadatas must not be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MockOutParametersMetadata empty() {
        return builder().build();
    }

    @Override
    public OutParameterMetadata getParameterMetadata(int index) {
        return this.outParameterMetadatas.get(index);
    }

    @Override
    public OutParameterMetadata getParameterMetadata(String name) {
        Assert.requireNonNull(name, "name must not be null");

        for (OutParameterMetadata parameterMetadata : this.outParameterMetadatas) {
            if (parameterMetadata.getName().equalsIgnoreCase(name)) {
                return parameterMetadata;
            }
        }

        throw new NoSuchElementException(String.format("Out Parameter %s not found", name));
    }

    @Override
    public List<OutParameterMetadata> getParameterMetadatas() {
        return this.outParameterMetadatas;
    }

    @Override
    public String toString() {
        return "MockOutParametersMetadata{" +
            "outParameterMetadatas=" + this.outParameterMetadatas +
            '}';
    }

    public static final class Builder {

        private final List<OutParameterMetadata> outParameterMetadatas = new ArrayList<>();

        private Builder() {
        }

        public MockOutParametersMetadata build() {
            return new MockOutParametersMetadata(this.outParameterMetadatas);
        }

        public Builder outParameterMetadata(OutParameterMetadata outParameterMetadata) {
            Assert.requireNonNull(outParameterMetadata, "outParameterMetadata must not be null");

            this.outParameterMetadatas.add(outParameterMetadata);
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                "outParameterMetadatas=" + this.outParameterMetadatas +
                '}';
        }

    }

}
