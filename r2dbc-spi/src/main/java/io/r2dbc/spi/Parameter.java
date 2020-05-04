/*
 * Copyright 2020 the original author or authors.
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
 * Represents a parameter to be interchanged. Parameters are typed and can define a value. Parameters without a value correspond with a SQL {@code NULL} value.
 * <p>
 * Parameters can be classified as {@link In input} or {@link Out output} parameters.
 *
 * @since 0.9
 */
public interface Parameter {

    /**
     * Returns the parameter.
     *
     * @return the type to be sent to the database.
     */
    Type getType();

    /**
     * Returns the value.
     *
     * @return the value for this parameter.  Value can be {@code null}.
     */
    @Nullable
    Object getValue();

    /**
     * Marker interface to classify a parameter as input parameter. Parameters that do not implement {@link Out} default to in parameters.
     */
    interface In {

    }

    /**
     * Marker interface to classify a parameter as output parameter. Parameters can implement both, {@code In} and {@code Out} interfaces to be classified as in-out parameters.
     */
    interface Out {

    }
}
