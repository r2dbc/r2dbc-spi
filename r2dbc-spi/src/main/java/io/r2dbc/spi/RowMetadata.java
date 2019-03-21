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

import java.util.NoSuchElementException;

/**
 * Represents the metadata for a row of the results returned from a query.
 */
public interface RowMetadata {

    /**
     * Returns the {@link ColumnMetadata} for one column in this row.
     *
     * @param identifier the identifier of the column
     * @return the {@link ColumnMetadata} for one column in this row
     * @throws IllegalArgumentException       if {@code identifier} is {@code null} or not supported
     * @throws NoSuchElementException         if there is no column with the name {@code identifier}
     * @throws ArrayIndexOutOfBoundsException if the {@code identifier} is a {@link Integer index} and it is less than zero or greater than the number of available columns.
     */
    ColumnMetadata getColumnMetadata(Object identifier);

    /**
     * Returns the {@link ColumnMetadata} for all columns in this row.
     *
     * @return the {@link ColumnMetadata} for all columns in this row
     */
    Iterable<? extends ColumnMetadata> getColumnMetadatas();

}
