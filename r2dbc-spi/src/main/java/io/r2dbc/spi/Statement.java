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

package io.r2dbc.spi;

import org.reactivestreams.Publisher;

/**
 * A statement that can be executed multiple times in a prepared and optimized way.
 *
 * @param <SELF> the concrete type of the statement
 */
public interface Statement<SELF extends Statement<SELF>> {

    /**
     * Save the current binding and create a new one.
     *
     * @return this {@link Statement}
     */
    SELF add();

    /**
     * Bind a value.
     *
     * @param identifier the identifier to bind to
     * @param value      the value to bind
     * @return this {@link Statement}
     * @throws IllegalArgumentException if {@code identifier} or {@code value} is {@code null}
     */
    SELF bind(Object identifier, Object value);

    /**
     * Bind a value to an index.  Indexes are zero-based.
     *
     * @param index the index to bind to
     * @param value the value to bind
     * @return this {@link Statement}
     * @throws IllegalArgumentException if {@code value} is {@code null}
     */
    SELF bind(int index, Object value);

    /**
     * Bind a value to an index.  Indexes are zero-based.
     *
     * @param index the index to bind to
     * @param value the value to bind
     * @return this {@link Statement}
     */
    default SELF bind(int index, boolean value) {
        return bind(index, (Boolean) value);
    }

    /**
     * Bind a value to an index.  Indexes are zero-based.
     *
     * @param index the index to bind to
     * @param value the value to bind
     * @return this {@link Statement}
     */
    default SELF bind(int index, byte value) {
        return bind(index, (Byte) value);
    }

    /**
     * Bind a value to an index.  Indexes are zero-based.
     *
     * @param index the index to bind to
     * @param value the value to bind
     * @return this {@link Statement}
     */
    default SELF bind(int index, char value) {
        return bind(index, (Character) value);
    }

    /**
     * Bind a value to an index.  Indexes are zero-based.
     *
     * @param index the index to bind to
     * @param value the value to bind
     * @return this {@link Statement}
     */
    default SELF bind(int index, double value) {
        return bind(index, (Double) value);
    }

    /**
     * Bind a value to an index.  Indexes are zero-based.
     *
     * @param index the index to bind to
     * @param value the value to bind
     * @return this {@link Statement}
     */
    default SELF bind(int index, float value) {
        return bind(index, (Float) value);
    }

    /**
     * Bind a value to an index.  Indexes are zero-based.
     *
     * @param index the index to bind to
     * @param value the value to bind
     * @return this {@link Statement}
     */
    default SELF bind(int index, int value) {
        return bind(index, (Integer) value);
    }

    /**
     * Bind a value to an index.  Indexes are zero-based.
     *
     * @param index the index to bind to
     * @param value the value to bind
     * @return this {@link Statement}
     */
    default SELF bind(int index, long value) {
        return bind(index, (Long) value);
    }

    /**
     * Bind a value to an index.  Indexes are zero-based.
     *
     * @param index the index to bind to
     * @param value the value to bind
     * @return this {@link Statement}
     */
    default SELF bind(int index, short value) {
        return bind(index, (Short) value);
    }

    /**
     * Bind a {@code null} value.
     *
     * @param identifier the identifier to bind to
     * @param type       the type of null value
     * @return this {@link Statement}
     * @throws IllegalArgumentException if {@code identifier} or {@code type} is {@code null}
     */
    SELF bindNull(Object identifier, Class<?> type);

    /**
     * Bind a {@code null} value.
     *
     * @param index the index to bind to
     * @param type  the type of null value
     * @return this {@link Statement}
     * @throws IllegalArgumentException if {@code type} is {@code null}
     */
    SELF bindNull(int index, Class<?> type);

    /**
     * Executes one or more SQL statements and returns the {@link Result}s.
     *
     * @return the {@link Result}s, returned by each statement
     */
    Publisher<? extends Result> execute();

}
