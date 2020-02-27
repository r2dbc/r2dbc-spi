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
 * Specification of properties to be used when starting a transaction
 *
 * @see Connection#beginTransaction(TransactionDefinition)
 * @since 0.9
 */
public interface TransactionDefinition {

    /**
     * Name of the isolation level transaction attribute.
     */
    String ISOLATION_LEVEL = "IsolationLevel";

    /**
     * Name of the read only transaction attribute.
     */
    String READ_ONLY = "ReadOnly";

    /**
     * Configures the isolation level for the transaction to start.
     *
     * @return the {@link IsolationLevel} to use. Can be {@code null} to indicate that the current isolation level should be used.
     */
    @Nullable
    default IsolationLevel getIsolationLevel() {
        return getAttribute(ISOLATION_LEVEL, IsolationLevel.class);
    }

    /**
     * Returns whether the transaction should be a read-only one or read-write by returning {@code true} respective {@code false}.
     *
     * @return {@code true} to specify a read-only transaction; {@code false} for a read-write transaction. Can be {@code null} to indicate that the current transaction mutability should be used.
     */
    @Nullable
    default Boolean isReadOnly() {
        return getAttribute(READ_ONLY, Boolean.class);
    }

    /**
     * Retrieve a transaction attribute by its name.  This low-level interface allows querying transaction attributes supported by the {@link Connection} that should be applied when starting a new
     * transaction.  The {@link Class type} parameter can be used to request a specific value type for easier consumption.  If a value cannot be represented as the requested type, then a
     * {@link ClassCastException} is thrown.
     *
     * @param name the name of the transaction attribute
     * @param type requested type of the attribute
     * @param <T>  requested value type
     * @return the value of the transaction attribute. Can be {@code null} to indicate absence of the attribute.
     * @throws IllegalArgumentException if {@code name} or {@code type} is {@code null}
     * @throws ClassCastException       if the value cannot be assigned to {@code type}
     */
    @Nullable
    <T> T getAttribute(String name, Class<T> type);

}
