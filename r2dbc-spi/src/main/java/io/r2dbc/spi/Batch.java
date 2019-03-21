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

import org.reactivestreams.Publisher;

/**
 * A collection of statements that are executed in a batch for performance reasons.
 */
public interface Batch {

    /**
     * Add a statement to this batch.
     *
     * @param sql the statement to add
     * @return this {@link Batch}
     * @throws IllegalArgumentException if {@code sql} is {@code null}
     */
    Batch add(String sql);

    /**
     * Executes one or more SQL statements and returns the {@link Result}s.
     *
     * @return the {@link Result}s, returned by each statement
     */
    Publisher<? extends Result> execute();
}
