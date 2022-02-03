/*
 * Copyright 2017-2022 the original author or authors.
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
import io.r2dbc.spi.Readable;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MockResult implements Result {

    private final Flux<Segment> segments;

    private MockResult(Flux<Segment> segments) {
        this.segments = Assert.requireNonNull(segments, "segments must not be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MockResult empty() {
        return builder().build();
    }

    @Override
    public Flux<Long> getRowsUpdated() {
        return this.segments.filter(UpdateCount.class::isInstance).cast(UpdateCount.class).map(UpdateCount::value).collect(Collectors.summingLong(Long::longValue)).flux();
    }

    @Override
    public <T> Flux<T> map(BiFunction<Row, RowMetadata, ? extends T> mappingFunction) {
        Assert.requireNonNull(mappingFunction, "mappingFunction must not be null");

        return this.segments.filter(RowSegment.class::isInstance).cast(RowSegment.class).map(it -> mappingFunction.apply(it.row(), it.row().getMetadata()));
    }

    @Override
    public <T> Publisher<T> map(Function<? super Readable, ? extends T> mappingFunction) {
        Assert.requireNonNull(mappingFunction, "f must not be null");

        return this.segments.filter(it -> it instanceof RowSegment || it instanceof OutSegment).map(it -> {

            if (it instanceof OutSegment) {
                return mappingFunction.apply(((OutSegment) it).outParameters());
            }

            return mappingFunction.apply(((RowSegment) it).row());
        });
    }

    @Override
    public String toString() {
        return "MockResult{" +
            "segments=" + this.segments +
            '}';
    }

    @Override
    public Result filter(Predicate<Segment> filter) {
        Assert.requireNonNull(filter, "mappingFunction must not be null");
        return new MockResult(this.segments.filter(filter));
    }

    @Override
    public <T> Publisher<T> flatMap(Function<Segment, ? extends Publisher<? extends T>> mappingFunction) {
        Assert.requireNonNull(mappingFunction, "mappingFunction must not be null");

        return this.segments.flatMap(mappingFunction);
    }

    public static UpdateCount updateCount(long value) {
        return () -> value;
    }

    public static RowSegment row(Row row) {
        Assert.requireNonNull(row, "row must not be null");

        return () -> row;
    }

    public static OutSegment outParameters(OutParameters parameters) {
        Assert.requireNonNull(parameters, "parameters must not be null");

        return () -> parameters;
    }

    public static final class Builder {

        private final List<Supplier<Segment>> segments = new ArrayList<>();

        private Builder() {
        }

        public MockResult build() {
            return new MockResult(Flux.fromIterable(this.segments).map(Supplier::get));
        }

        public Builder segment(Segment... segments) {
            Assert.requireNonNull(segments, "segments must not be null");

            Stream.of(segments)
                .peek(segment -> Assert.requireNonNull(segment, "segment must not be null"))
                .<Supplier<Segment>>map(segment -> (() -> segment))
                .forEach(this.segments::add);

            return this;
        }

        public Builder row(Row... rows) {
            Assert.requireNonNull(rows, "rows must not be null");

            Stream.of(rows)
                .peek(row -> Assert.requireNonNull(row, "row must not be null"))
                .<Supplier<Segment>>map(row -> () -> MockResult.row(row))
                .forEach(this.segments::add);

            return this;
        }

        /**
         * @param rowMetadata metadata for a {@link Row}
         * @return {@code this} {@link Builder}
         * @deprecated since 0.9, no-op. Provide metadata as part of {@link Row#getMetadata()}.
         */
        @Deprecated
        public Builder rowMetadata(RowMetadata rowMetadata) {
            return this;
        }

        public Builder rowsUpdated(long rowsUpdated) {

            this.segments.add(() -> updateCount(rowsUpdated));
            return this;
        }

        public Builder outParameters(OutParameters outParameters) {
            Assert.requireNonNull(outParameters, "outParameters must not be null");

            this.segments.add(() -> MockResult.outParameters(outParameters));

            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                ", segments=" + this.segments +
                '}';
        }

    }

}
