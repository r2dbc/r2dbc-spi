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

import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MockStatement implements Statement {

    private final List<Map<Object, Object>> bindings = new ArrayList<>();

    private final Flux<Result> results;

    private boolean addCalled = false;

    private Map<Object, Object> current;

    private String[] generatedValuesColumns;

    private MockStatement(Flux<Result> results) {
        this.results = Assert.requireNonNull(results, "results must not be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MockStatement empty() {
        return builder().build();
    }

    @Override
    public MockStatement add() {
        this.addCalled = true;
        this.current = null;
        return this;
    }

    @Override
    public MockStatement bind(Object identifier, Object value) {
        Assert.requireNonNull(identifier, "identifier must not be null");
        Assert.requireNonNull(value, "value must not be null");

        getCurrent().put(identifier, value);
        return this;
    }

    @Override
    public MockStatement bind(int index, Object value) {
        Assert.requireNonNull(value, "value must not be null");

        getCurrent().put(index, value);
        return this;
    }

    @Override
    public MockStatement bindNull(Object identifier, Class<?> type) {
        Assert.requireNonNull(identifier, "identifier must not be null");
        Assert.requireNonNull(type, "type must not be null");

        getCurrent().put(identifier, type);
        return this;
    }

    @Override
    public MockStatement bindNull(int index, Class<?> type) {
        Assert.requireNonNull(type, "type must not be null");

        getCurrent().put(index, type);
        return this;
    }

    @Override
    public Flux<Result> execute() {
        return this.results;
    }

    public List<Map<Object, Object>> getBindings() {
        return this.bindings;
    }

    public String[] getGeneratedValuesColumns() {
        return this.generatedValuesColumns;
    }

    public boolean isAddCalled() {
        return this.addCalled;
    }

    @Override
    public Statement returnGeneratedValues(String... columns) {
        this.generatedValuesColumns = columns;
        return this;
    }

    @Override
    public String toString() {
        return "MockStatement{" +
            "bindings=" + bindings +
            ", results=" + results +
            ", addCalled=" + addCalled +
            ", current=" + current +
            ", generatedValuesColumns=" + Arrays.toString(generatedValuesColumns) +
            '}';
    }

    private Map<Object, Object> getCurrent() {
        if (this.current == null) {
            this.current = new HashMap<>();
            this.bindings.add(this.current);
        }

        return this.current;
    }

    public static final class Builder {

        private final List<Result> results = new ArrayList<>();

        private Builder() {
        }

        public MockStatement build() {
            return new MockStatement(Flux.fromIterable(this.results));
        }

        public Builder result(Result result) {
            Assert.requireNonNull(result, "result must not be null");

            this.results.add(result);
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                "results=" + this.results +
                '}';
        }

    }

}
