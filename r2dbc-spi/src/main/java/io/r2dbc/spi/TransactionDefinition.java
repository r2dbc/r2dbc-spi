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

import java.time.Duration;

/**
 * Specification of properties to be used when starting a transaction.  This interface is typically implemented by code that calls {@link Connection#beginTransaction(TransactionDefinition)}.
 *
 * @see Connection#beginTransaction(TransactionDefinition)
 * @see Option
 * @since 0.9
 */
public interface TransactionDefinition {

    /**
     * Isolation level requested for the transaction.
     */
    Option<IsolationLevel> ISOLATION_LEVEL = Option.valueOf("isolationLevel");

    /**
     * The transaction mutability (i.e. whether the transaction should be started in read-only mode).
     */
    Option<Boolean> READ_ONLY = Option.valueOf("readOnly");

    /**
     * Name of the transaction.
     */
    Option<String> NAME = Option.valueOf("name");

    /**
     * Lock wait timeout.
     */
    Option<Duration> LOCK_WAIT_TIMEOUT = Option.valueOf("lockWaitTimeout");

    /**
     * Retrieve a transaction attribute by its {@link Option} identifier.  This low-level interface allows querying transaction attributes supported by the {@link Connection} that should be applied
     * when starting a new transaction.
     *
     * @param option the option to retrieve the value for
     * @param <T>    requested value type
     * @return the value of the transaction attribute. Can be {@code null} to indicate absence of the attribute.
     * @throws IllegalArgumentException if {@code name} or {@code type} is {@code null}
     */
    @Nullable
    <T> T getAttribute(Option<T> option);

}
