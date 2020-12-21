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

/**
 * Indicates that an instance wraps a concrete implementation of an R2DBC SPI interface.  A wrapper is expected to implement this interface so that callers can extract the original instance.
 *
 * @param <T> The R2DBC SPI type being wrapped
 */
public interface Wrapped<T> {

    /**
     * Returns the original instance wrapped by this object.
     *
     * @return the original instance wrapped by this object
     */
    T unwrap();

    /**
     * Recursively {@link #unwrap()} the object and returns the first instance that
     * matches to the given {@link Class} or {@code null} if none matches.
     * <p>
     * If this object is an instance of the given {@link Class}, this object is returned.
     *
     * @param targetClass target type to unwrap
     * @param <E>         returning type
     * @return unwrapped instance of given type or {@code null}
     * @throws IllegalArgumentException if {@code targetClass} is {@code null}
     * @since 0.9.0
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <E> E unwrap(Class<E> targetClass) {
        Assert.requireNonNull(targetClass, "targetClass must not be null");

        if (targetClass.isInstance(this)) {
            return (E) this;
        }
        T unwrapped = this.unwrap();
        if (!(unwrapped instanceof Wrapped)) {
            return null;
        }
        return ((Wrapped<?>) unwrapped).unwrap(targetClass);
    }

}
