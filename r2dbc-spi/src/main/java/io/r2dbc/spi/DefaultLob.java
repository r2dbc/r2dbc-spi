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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Abstract implementation for large object types.
 */
class DefaultLob<T> {

    @SuppressWarnings("unchecked")
    private static final AtomicIntegerFieldUpdater<DefaultLob<?>> ATOMIC_DISCARD = (AtomicIntegerFieldUpdater) AtomicIntegerFieldUpdater.newUpdater(DefaultLob.class, "discarded");

    @SuppressWarnings("unchecked")
    private static final AtomicIntegerFieldUpdater<DefaultLob<?>> ATOMIC_CONSUMED = (AtomicIntegerFieldUpdater) AtomicIntegerFieldUpdater.newUpdater(DefaultLob.class, "consumed");

    private static final int NOT_DISCARDED = 0;

    private static final int DISCARDED = 1;

    private static final int NOT_CONSUMED = 0;

    private static final int CONSUMED = 1;

    private final Publisher<T> p;

    private volatile int discarded = NOT_DISCARDED;

    private volatile int consumed = NOT_CONSUMED;

    /**
     * Creates a new {@link DefaultLob} instance.
     *
     * @param p
     */
    DefaultLob(Publisher<T> p) {
        this.p = p;
    }

    /**
     * Returns the content stream.
     *
     * @return the content stream.
     */
    Publisher<T> stream() {
        return subscriber -> {

            if (ATOMIC_DISCARD.get(this) == DISCARDED) {
                subscriber.onError(new IllegalStateException("Source stream was already released"));
                return;
            }

            if (!ATOMIC_CONSUMED.compareAndSet(this, NOT_CONSUMED, CONSUMED)) {
                subscriber.onError(new IllegalStateException("Source stream was already consumed"));
                return;
            }

            this.p.subscribe(subscriber);
        };
    }

    /**
     * Discard the content stream.
     *
     * @return
     */
    Publisher<Void> discard() {
        return outer -> {

            AtomicBoolean completed = new AtomicBoolean();

            if (!ATOMIC_DISCARD.compareAndSet(this, NOT_DISCARDED, DISCARDED)) {
                outer.onError(new IllegalStateException("Source stream was already released"));
                return;
            }

            try {
                this.p.subscribe(new Subscriber<T>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        s.cancel();
                        if (completed.compareAndSet(false, true)) {
                            outer.onComplete();
                        }
                    }

                    @Override
                    public void onNext(T t) {
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (completed.compareAndSet(false, true)) {
                            outer.onError(new IllegalStateException("Resource release has failed", t));
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (completed.compareAndSet(false, true)) {
                            outer.onComplete();
                        }
                    }
                });
            } catch (Exception e) {
                if (completed.compareAndSet(false, true)) {
                    outer.onError(new IllegalStateException("Resource release has failed", e));
                }
            }
        };
    }

}
