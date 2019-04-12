/*
 * Copyright 2019 the original author or authors.
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

package io.r2dbc.spi;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Represents a handle to a large character object.
 */
public interface Clob {

    /**
     * Creates a new {@link Clob} wrapper that is backed by a {@link Publisher} of {@link CharSequence} or its subtypes.
     * The wrapper subscribes and cancels the subscription immediately on {@link #discard()}.
     *
     * @param p the backing {@link Publisher} of {@link CharSequence}.
     * @return the {@link Clob} wrapper
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static Clob from(Publisher<? extends CharSequence> p) {
        Assert.requireNonNull(p, "Publisher must not be null");

        DefaultLob<CharSequence> lob = new DefaultLob(p);

        return new Clob() {

            @Override
            public Publisher<CharSequence> stream() {
                return lob.stream();
            }

            @Override
            public Publisher<Void> discard() {
                return lob.discard();
            }
        };
    }

    /**
     * Returns the content stream as a {@link Publisher} emitting {@link CharSequence} chunks.
     * <p>
     * The content stream can be consumed ("subscribed to") only once. Subsequent consumptions result in a {@link IllegalStateException}.
     * <p>
     * Once {@link Publisher#subscribe(Subscriber) subscribed}, {@link Subscription#cancel() canceling} the subscription releases resources associated with this {@link Clob}.
     *
     * @return a {@link Publisher} emitting {@link CharSequence} chunks.
     */
    Publisher<CharSequence> stream();

    /**
     * Release any resources held by the {@link Clob} when not subscribing to the {@link #stream() stream content}.
     *
     * @return a {@link Publisher} that termination is complete
     */
    Publisher<Void> discard();
}
