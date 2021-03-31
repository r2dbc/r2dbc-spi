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

package io.r2dbc.spi;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents the results of a query against a database.  Results can be consumed only once by either consuming {@link #getRowsUpdated()} or {@link #map(BiFunction)}.
 *
 * <p>A {@link Result} object maintains a consumption state that may be backed by a cursor pointing
 * to its current row of data.  A {@link Result} allows read-only and forward-only consumption of statement results.
 * Thus, you can consume either {@link #getRowsUpdated()} or {@link #map(BiFunction) Rows} through it only once and only from the first row to the last row.
 */
public interface Result {

    /**
     * A mapping function container that is used to map interleaved result types of unknown order.
     *
     * <p>Databases may produce interleaved update counts or result sets without explicit knowledge of order by clients.</p>
     */
    interface Mapping<T> {

        /**
         * The function translating update counts to a custom type.
         *
         * @see #getRowsUpdated()
         */
        Function<Integer, ? extends Publisher<T>> rowsUpdated();

        /**
         * The function translating rows to a custom type.
         *
         * @see #map(BiFunction)
         */
        BiFunction<Row, RowMetadata, ? extends Publisher<T>> rowsFetched();
    }

    /**
     * Returns the number of rows updated by a query or a mapping of the rows that are the results of a query against a database. May be empty.
     *
     * @param mapping The mapping function container holding implementations of {@link #getRowsUpdated()} and {@link #map(BiFunction)}.
     * @throws IllegalArgumentException if {@code mapping} is {@code null}
     * @throws IllegalStateException if the result was consumed
     */
    <T> Publisher<T> map(Mapping<T> mapping);

    /**
     * Returns the number of rows updated by a query against a database.  May be empty if the query did not update any rows.
     *
     * @return the number of rows updated by a query against a database
     * @throws IllegalStateException if the result was consumed
     */
    default Publisher<Integer> getRowsUpdated() {
        return map(new Mapping<Integer>() {
            @Override
            public Function<Integer, Publisher<Integer>> rowsUpdated() {
                return SingleValuePublisher::new;
            }

            @Override
            public BiFunction<Row, RowMetadata, Publisher<Integer>> rowsFetched() {
                return (r, m) -> new EmptyPublisher<>();
            }
        });
    }

    /**
     * Returns a mapping of the rows that are the results of a query against a database.  May be empty if the query did not return any rows.  A {@link Row} can be only considered valid within a
     * {@link BiFunction mapping function} callback.
     *
     * @param mappingFunction the {@link BiFunction} that maps a {@link Row} and {@link RowMetadata} to a value
     * @param <T>             the type of the mapped value
     * @return a mapping of the rows that are the results of a query against a database
     * @throws IllegalArgumentException if {@code mappingFunction} is {@code null}
     * @throws IllegalStateException    if the result was consumed
     */
    default <T> Publisher<T> map(BiFunction<Row, RowMetadata, ? extends T> mappingFunction) {
        return map(new Mapping<T>() {
            @Override
            public Function<Integer, ? extends Publisher<T>> rowsUpdated() {
                return i -> new EmptyPublisher<>();
            }

            @Override
            public BiFunction<Row, RowMetadata, ? extends Publisher<T>> rowsFetched() {
                return (r, m) -> new SingleValuePublisher<>(mappingFunction.apply(r, m));
            }
        });
    }
}

final class SingleValuePublisher<T> implements Publisher<T> {
    private final T t;

    SingleValuePublisher(T t) {
        this.t = t;
    }

    @Override
    public void subscribe(Subscriber<? super T> s) {
        s.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                s.onNext(t);
                s.onComplete();
            }

            @Override
            public void cancel() {}
        });
    }
}

final class EmptyPublisher<T> implements Publisher<T> {

    @Override
    public void subscribe(Subscriber<? super T> s) {
        s.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                s.onComplete();
            }

            @Override
            public void cancel() {}
        });
    }
}