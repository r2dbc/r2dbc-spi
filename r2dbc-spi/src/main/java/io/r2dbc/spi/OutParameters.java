/*
 * Copyright 2021 the original author or authors.
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
 * Represents a set of {@code OUT} parameters returned from a stored procedure.
 * Values from out parameters can be either retrieved by specifying a parameter name or the parameter index.
 * Parameter indexes are {@code 0}-based.
 *
 * <p> Parameter names used as input to getter methods are case insensitive.
 * When a get method is called with a parameter name and several parameters have the same name, then the value of the first matching parameter will be returned.
 * Parameters that are not explicitly named in the query should be referenced through parameter indexes.
 *
 * <p>For maximum portability, parameters within each {@link OutParameters} should be read in left-to-right order, and each parameter should be read only once.
 *
 * <p>{@link #get(String)} and {@link #get(int)} without specifying a target type returns a suitable value representation.  The R2DBC specification contains a mapping table that shows default
 * mappings between database types and Java types.
 * Specifying a target type, the R2DBC driver attempts to convert the value to the target type.
 * <p>A parameter is invalidated after consumption.
 * <p>The number, type and characteristics of parameters are described through {@link OutParametersMetadata}.
 *
 * @since 0.9
 */
public interface OutParameters extends Gettable {

    /**
     * Returns the {@link OutParametersMetadata} for all out parameters.
     *
     * @return the {@link OutParametersMetadata} for all out parameters
     */
    OutParametersMetadata getMetadata();

}
