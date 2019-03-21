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

}
