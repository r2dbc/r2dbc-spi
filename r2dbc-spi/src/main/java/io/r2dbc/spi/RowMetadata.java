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

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents the metadata for a row of the results returned from a query.
 * Metadata for columns can be either retrieved by specifying a column name or the column index.
 * Columns indexes are {@code 0}-based.  Column names do not necessarily reflect the column names how they are in the underlying tables but rather how columns are represented (e.g. aliased) in the
 * result.
 */
public interface RowMetadata {

    /**
     * Returns the {@link ColumnMetadata} for one column in this row.
     *
     * @param index the column index starting at 0
     * @return the {@link ColumnMetadata} for one column in this row
     * @throws IndexOutOfBoundsException if {@code index} is out of range (negative or equals/exceeds {@code getColumnMetadatas().size()})
     */
    ColumnMetadata getColumnMetadata(int index);

    /**
     * Returns the {@link ColumnMetadata} for one column in this row.
     *
     * @param name the name of the column.  Column names are case insensitive.  When a get method contains several columns with same name, then the value of the first matching column will be returned
     * @return the {@link ColumnMetadata} for one column in this row
     * @throws IllegalArgumentException if {@code name} is {@code null}
     * @throws NoSuchElementException   if there is no column with the {@code name}
     */
    ColumnMetadata getColumnMetadata(String name);

    /**
     * Returns the {@link ColumnMetadata} for all columns in this row.
     *
     * @return the {@link ColumnMetadata} for all columns in this row
     */
    List<? extends ColumnMetadata> getColumnMetadatas();

    /**
     * Returns an unmodifiable collection of column names.
     * <p>
     * Any attempts to modify the returned collection, whether direct or via its iterator, result in an {@link UnsupportedOperationException}.
     * <p>
     * The iteration order of the column names depends on the actual query result.
     * Column names may appear multiple times if the result specifies multiple columns with the same name.
     * Lookups through {@link Collection#contains(Object)} are case-insensitive.
     * Implementations may enhance comparison sorting rules with escape characters to enforce a particular mode of comparison
     * when querying for presence/absence of a column.
     *
     * @return the column names.
     * @deprecated since 0.9 for removal. Use {@link #contains(String)} or {@link #getColumnMetadatas()} instead.
     */
    @Deprecated
    Collection<String> getColumnNames();

    /**
     * Returns whether this object contains metadata for {@code columnName}.
     * Lookups are case-insensitive. Implementations may allow escape characters to enforce a particular mode of comparison
     * when querying for presence/absence of a column.
     *
     * @return {@code true} if this object contains metadata for {@code columnName}; {@code false} otherwise.
     * @since 0.9
     */
    default boolean contains(String columnName) {
        return getColumnNames().contains(columnName);
    }

}
