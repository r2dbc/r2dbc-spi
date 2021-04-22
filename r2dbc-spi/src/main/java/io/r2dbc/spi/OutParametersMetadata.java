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

import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Represents the metadata for {@code OUT} parameters of the results returned from a stored procedure.
 * Metadata for parameters can be either retrieved by specifying a parameter name or the parameter index.
 * Parameter indexes are {@code 0}-based.
 */
public interface OutParametersMetadata {

    /**
     * Returns the {@link OutParameterMetadata} for one parameter.
     *
     * @param index the parameter index starting at 0
     * @return the {@link OutParameterMetadata} for one parameter
     * @throws ArrayIndexOutOfBoundsException if the {@code index} is less than zero or greater than the number of available parameters.
     */
    OutParameterMetadata getParameterMetadata(int index);

    /**
     * Returns the {@link OutParameterMetadata} for one parameter.
     *
     * @param name the name of the parameter.  Parameter names are case insensitive.  When a get method contains several parameters with same name, then the value of the first matching parameter
     *             will be returned
     * @return the {@link OutParameterMetadata} for one parameter
     * @throws IllegalArgumentException if {@code name} is {@code null}
     * @throws NoSuchElementException   if there is no parameter with the {@code name}
     */
    OutParameterMetadata getParameterMetadata(String name);

    /**
     * Returns the {@link OutParameterMetadata} for all parameters.
     *
     * @return the {@link OutParameterMetadata} for all parameters
     */
    Iterable<? extends OutParameterMetadata> getParameterMetadatas();

    /**
     * Returns an unmodifiable collection of parameter names.
     * <p>
     * Any attempts to modify the returned collection, whether direct or via its iterator, result in an {@link UnsupportedOperationException}.
     * <p>
     * The iteration order of the parameter names depends on the actual stored procedure.
     * Parameter names may appear multiple times if the result specifies multiple parameters with the same name.
     * Lookups through {@link Collection#contains(Object)} are case-insensitive.
     * Drivers may enhance comparison sorting rules with escape characters to enforce a particular mode of comparison
     * when querying for presence/absence of a parameter.
     *
     * @return the parameter names.
     */
    Collection<String> getParameterNames();

}
