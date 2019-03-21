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
 * Represents a row returned from a database query.
 */
public interface Row {

    /**
     * Returns the value for a column in this row.
     *
     * @param identifier the identifier of the column
     * @param type       the type of item to return. This type must be assignable to, and allows for variance.
     * @param <T>        the type of the item being returned
     * @return the value for a column in this row.  Value can be {@code null}.
     * @throws IllegalArgumentException if {@code identifier} or {@code type} is {@code null}
     */
    @Nullable
    <T> T get(Object identifier, Class<T> type);

    /**
     * Returns the value for a column in this row using the default type mapping.  The default implementation of this method calls {@link #get(Object, Class)} passing {@link Object} as the type in
     * order to allow the implementation to make the loosest possible match.
     *
     * @param identifier the identifier of the column
     * @return the value for a column in this row.  Value can be {@code null}.
     * @throws IllegalArgumentException if {@code identifier} or {@code type} is {@code null}
     */
    @Nullable
    default Object get(Object identifier) {
        return get(identifier, Object.class);
    }

}
