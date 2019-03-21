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

import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Result;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public final class MockBatch implements Batch {

    private final Flux<Result> results;

    private final List<String> sqls = new ArrayList<>();

    private MockBatch(Flux<Result> results) {
        this.results = Assert.requireNonNull(results, "results must not be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MockBatch empty() {
        return builder().build();
    }

    @Override
    public MockBatch add(String sql) {
        Assert.requireNonNull(sql, "sql must not be null");

        this.sqls.add(sql);
        return this;
    }

    @Override
    public Flux<Result> execute() {
        return this.results;
    }

    public List<String> getSqls() {
        return this.sqls;
    }

    @Override
    public String toString() {
        return "MockBatch{" +
            "results=" + this.results +
            ", sqls=" + this.sqls +
            '}';
    }

    public static final class Builder {

        private final List<Result> results = new ArrayList<>();

        private Builder() {
        }

        public MockBatch build() {
            return new MockBatch(Flux.fromIterable(this.results));
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
