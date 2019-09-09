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
import java.util.NoSuchElementException;

/**
 * Represents the metadata for a row of the results returned from a query.
 * Metadata for columns can be either retrieved by specifying a column name or the column index.
 * Columns are numbered from 0.  Column names do not necessarily reflect the column names how they are in the underlying tables but rather how columns are represented (e.g. aliased) in the result.
 */
public interface RowMetadata {

    /**
     * Returns the {@link ColumnMetadata} for one column in this row.
     *
     * @param identifier the identifier of the column. Can be either the column index starting at 0 or column name. Column names are case insensitive.  When a get method is called with a column
     *                   name and several columns have the same name, then the value of the first matching column will be returned.
     * @return the {@link ColumnMetadata} for one column in this row
     * @throws IllegalArgumentException       if {@code identifier} is {@code null} or not supported
     * @throws NoSuchElementException         if there is no column with the name {@code identifier}
     * @throws ArrayIndexOutOfBoundsException if the {@code identifier} is a {@link Integer index} and it is less than zero or greater than the number of available columns.
     * @deprecated Use {@link #getColumnMetadata(int)} or {@link #getColumnMetadata(String)} instead
     */
    @Deprecated
    ColumnMetadata getColumnMetadata(Object identifier);

    /**
     * Returns the {@link ColumnMetadata} for one column in this row.  The default implementation of this method calls {@link #getColumnMetadata(Object)} to allow SPI change in a less-breaking way.
     *
     * @param index the column index starting at 0
     * @return the {@link ColumnMetadata} for one column in this row
     * @throws ArrayIndexOutOfBoundsException if the {@code index} is less than zero or greater than the number of available columns.
     */
    default ColumnMetadata getColumnMetadata(int index) {
        return getColumnMetadata((Object) index);
    }

    /**
     * Returns the {@link ColumnMetadata} for one column in this row.  The default implementation of this method calls {@link #getColumnMetadata(Object)} to allow SPI change in a less-breaking way.
     *
     * @param name the name of the column.  Column names are case insensitive.  When a get method contains several columns with same name, then the value of the first matching column will be returned
     * @return the {@link ColumnMetadata} for one column in this row
     * @throws IllegalArgumentException if {@code name} is {@code null}
     * @throws NoSuchElementException   if there is no column with the {@code name}
     */
    default ColumnMetadata getColumnMetadata(String name) {
        return getColumnMetadata((Object) name);
    }

    /**
     * Returns the {@link ColumnMetadata} for all columns in this row.
     *
     * @return the {@link ColumnMetadata} for all columns in this row
     */
    Iterable<? extends ColumnMetadata> getColumnMetadatas();

    /**
     * Returns an unmodifiable collection of column names.
     * <p>
     * Any attempts to modify the returned collection, whether direct or via its iterator, result in an {@link UnsupportedOperationException}.
     * <p>
     * The iteration order of the column names depends on the actual query result.
     * Column names may appear multiple times if the result specifies multiple columns with the same name.
     * Lookups through {@link Collection#contains(Object)} are case-insensitive.
     * Drivers may enhance comparison sorting rules with escape characters to enforce a particular mode of comparison
     * when querying for presence/absence of a column.
     *
     * @return the column names.
     */
    Collection<String> getColumnNames();

}
