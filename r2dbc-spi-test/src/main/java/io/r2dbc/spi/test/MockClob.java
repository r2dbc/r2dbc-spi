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

import io.r2dbc.spi.Clob;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class MockClob implements Clob {

    private final Flux<CharSequence> stream;

    private boolean discardCalled = false;

    private MockClob(Flux<CharSequence> stream) {
        this.stream = Assert.requireNonNull(stream, "stream must not be null");
    }

    public static MockClob.Builder builder() {
        return new MockClob.Builder();
    }

    public static MockClob empty() {
        return builder().build();
    }

    @Override
    public Publisher<Void> discard() {
        this.discardCalled = true;
        return Mono.empty();
    }

    public boolean isDiscardCalled() {
        return this.discardCalled;
    }

    @Override
    public Publisher<CharSequence> stream() {
        return this.stream;
    }

    @Override
    public String toString() {
        return "MockClob{" +
            "stream=" + this.stream +
            ", discardCalled=" + this.discardCalled +
            '}';
    }

    public static final class Builder {

        private final List<CharSequence> items = new ArrayList<>();

        private Builder() {
        }

        public MockClob build() {
            return new MockClob(Flux.fromIterable(this.items));
        }

        public Builder item(CharSequence... items) {
            Assert.requireNonNull(items, "items must not be null");

            Stream.of(items)
                .peek(item -> Assert.requireNonNull(item, "item must not be null"))
                .forEach(this.items::add);

            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                "items=" + this.items +
                '}';
        }
    }
}
