/*
 * Copyright 2017-2022 the original author or authors.
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

import java.util.function.BiFunction;

/**
 * Represents a row returned from a database query.
 * Values from columns can be either retrieved by specifying a column name or the column index.
 * Column indexes are {@code 0}-based.  Column names do not necessarily reflect the column names how they are in the underlying tables but rather how columns are represented (e.g. aliased) in the
 * result.
 *
 * <p> Column names used as input to getter methods are case insensitive.
 * When a get method is called with a column name and several columns have the same name, then the value of the first matching column will be returned.
 * The column name option is designed to be used when column names are used in the SQL query that generated the result set.
 * Columns that are not explicitly named in the query should be referenced through column indexes.
 *
 * <p>For maximum portability, result columns within each {@link Row} should be read in left-to-right order, and each column should be read only once.
 *
 * <p>{@link #get(String)} and {@link #get(int)} without specifying a target type returns a suitable value representation.  The R2DBC specification contains a mapping table that shows default
 * mappings between database types and Java types.
 * Specifying a target type, the R2DBC driver attempts to convert the value to the target type.
 * <p>A row is invalidated after consumption in the {@link Result#map(BiFunction) mapping function}.
 * <p>The number, type and characteristics of columns are described through {@link RowMetadata}
 */
public interface Row extends Readable {

    /**
     * Returns the {@link RowMetadata} for all columns in this row.
     *
     * @return the {@link RowMetadata} for all columns in this row
     * @since 0.9
     */
    RowMetadata getMetadata();

}
