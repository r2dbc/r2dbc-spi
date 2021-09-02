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

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents the metadata for {@code OUT} parameters of the results returned from a stored procedure.
 * Metadata for parameters can be either retrieved by specifying a out parameter name or the out parameter index.
 * Parameter indexes are {@code 0}-based.
 */
public interface OutParametersMetadata {

    /**
     * Returns the {@link OutParameterMetadata} for one out parameter.
     *
     * @param index the out parameter index starting at 0
     * @return the {@link OutParameterMetadata} for one out parameter
     * @throws IndexOutOfBoundsException if {@code index} is out of range (negative or equals/exceeds {@code getParameterMetadatas().size()})
     */
    OutParameterMetadata getParameterMetadata(int index);

    /**
     * Returns the {@link OutParameterMetadata} for one parameter.
     *
     * @param name the name of the out parameter.  Parameter names are case insensitive.
     * @return the {@link OutParameterMetadata} for one out parameter
     * @throws IllegalArgumentException if {@code name} is {@code null}
     * @throws NoSuchElementException   if there is no out parameter with the {@code name}
     */
    OutParameterMetadata getParameterMetadata(String name);

    /**
     * Returns the {@link OutParameterMetadata} for all out parameters.
     *
     * @return the {@link OutParameterMetadata} for all out parameters
     */
    List<? extends OutParameterMetadata> getParameterMetadatas();

}
