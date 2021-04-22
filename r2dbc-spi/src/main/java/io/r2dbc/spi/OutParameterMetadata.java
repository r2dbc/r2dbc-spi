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
 * Represents the metadata for an {@code OUT} parameter.  The implementation of all methods except {@link #getName()}  is optional for drivers.  Parameter metadata is optionally
 * available as by-product of statement execution on a best-effort basis.
 *
 * @since 0.9
 */
public interface OutParameterMetadata extends ReadableMetadata {

}
