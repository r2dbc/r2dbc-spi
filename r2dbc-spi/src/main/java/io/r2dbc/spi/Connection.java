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
 * A single connection to a database.  SQL statements are executed and results are returned within the context of a connection.  A {@link Connection} object can consist of any number of transport
 * connections to the underlying database or represent a session over a multiplexed transport connection.  For maximum portability, connections should be used synchronously.
 * <p>
 * {@link Connection} objects initiate database conversations for transaction management and statement execution.  Objects created by a connection are only valid as long as the connection remains
 * open.
 * <p>
 * When configuring a connection, R2DBC applications should use the appropriate methods such as {@link #beginTransaction()}, {@link #setAutoCommit(boolean)} and
 * {@link #setTransactionIsolationLevel(IsolationLevel)} to change transaction properties instead.  Applications should not execute SQL commands directly to change the connection configuration when
 * a R2DBC method is available.
 * <p>
 * New connections are by default created in {@link #setAutoCommit(boolean) auto-commit mode}.
 *
 * @see Statement
 * @see Result
 * @see Row
 */
public interface Connection extends Closeable {

    /**
     * Begins a new transaction.  Calling this method disables {@link #isAutoCommit() auto-commit} mode.
     *
     * @return a {@link Publisher} that indicates that the transaction is open
     */
    Publisher<Void> beginTransaction();

    /**
     * Begins a new transaction.  Calling this method disables {@link #isAutoCommit() auto-commit} mode.
     *
     * @param definition attributes for the transaction.
     * @return a {@link Publisher} that indicates that the transaction is open
     * @since 0.9
     */
    Publisher<Void> beginTransaction(TransactionDefinition definition);

    /**
     * Release any resources held by the {@link Connection}.
     *
     * @return a {@link Publisher} that termination is complete
     */
    @Override
    Publisher<Void> close();

    /**
     * Commits the current transaction.
     *
     * @return a {@link Publisher} that indicates that a transaction has been committed
     */
    Publisher<Void> commitTransaction();

    /**
     * Creates a new {@link Batch} instance for building a batched request.
     *
     * @return a new {@link Batch} instance
     */
    Batch createBatch();

    /**
     * Creates a savepoint in the current transaction.
     *
     * @param name the name of the savepoint to create
     * @return a {@link Publisher} that indicates that a savepoint has been created
     * @throws IllegalArgumentException      if {@code name} is {@code null}
     * @throws UnsupportedOperationException if savepoints are not supported
     */
    Publisher<Void> createSavepoint(String name);

    /**
     * Creates a new statement for building a statement-based request.
     *
     * @param sql the SQL of the statement
     * @return a new {@link Statement} instance
     * @throws IllegalArgumentException if {@code sql} is {@code null}
     */
    Statement createStatement(String sql);

    /**
     * Returns the auto-commit mode for this connection.
     *
     * @return {@literal true} if the connection is in auto-commit mode; {@literal false} otherwise.
     */
    boolean isAutoCommit();

    /**
     * Returns the {@link ConnectionMetadata} about the product this {@link Connection} is connected to.
     *
     * @return the {@link ConnectionMetadata} about the product this {@link Connection} is connected to
     */
    ConnectionMetadata getMetadata();

    /**
     * Returns the {@link IsolationLevel} for this connection.
     * <p>Isolation level is typically one of the following constants:
     *
     * <ul>
     * <li>{@link IsolationLevel#READ_UNCOMMITTED}</li>
     * <li>{@link IsolationLevel#READ_COMMITTED}</li>
     * <li>{@link IsolationLevel#REPEATABLE_READ}</li>
     * <li>{@link IsolationLevel#SERIALIZABLE}</li>
     * </ul>
     * <p>
     * {@link IsolationLevel} is extensible so drivers can return a vendor-specific {@link IsolationLevel}.
     *
     * @return the {@link IsolationLevel} for this connection.
     */
    IsolationLevel getTransactionIsolationLevel();

    /**
     * Releases a savepoint in the current transaction.  Calling this for drivers not supporting savepoint release results in a no-op.
     *
     * @param name the name of the savepoint to release
     * @return a {@link Publisher} that indicates that a savepoint has been released
     * @throws IllegalArgumentException if {@code name} is {@code null}
     */
    Publisher<Void> releaseSavepoint(String name);

    /**
     * Rolls back the current transaction.
     *
     * @return a {@link Publisher} that indicates that a transaction has been rolled back
     */
    Publisher<Void> rollbackTransaction();

    /**
     * Rolls back to a savepoint in the current transaction.
     *
     * @param name the name of the savepoint to rollback to
     * @return a {@link Publisher} that indicates that a savepoint has been rolled back to
     * @throws IllegalArgumentException      if {@code name} is {@code null}
     * @throws UnsupportedOperationException if savepoints are not supported
     */
    Publisher<Void> rollbackTransactionToSavepoint(String name);

    /**
     * Configures the auto-commit mode for the current transaction.
     * If a connection is in auto-commit mode, then all {@link Statement}s will be executed and committed as individual transactions.
     * Otherwise, in explicit transaction mode, transactions have to be {@link #beginTransaction() started} explicitly.
     * A transaction needs to be either {@link #commitTransaction() committed} or {@link #rollbackTransaction() rolled back} to clean up the transaction state.
     * <p>
     * Calling this method during an active transaction and the
     * auto-commit mode is changed, the transaction is committed.  Calling this method without changing auto-commit mode this invocation results in a no-op.
     *
     * @param autoCommit the isolation level for this transaction
     * @return a {@link Publisher} that indicates that auto-commit mode has been configured
     */
    Publisher<Void> setAutoCommit(boolean autoCommit);

    /**
     * Configures the isolation level for the current transaction.
     * <p>Isolation level is typically one of the following constants:
     *
     * <ul>
     * <li>{@link IsolationLevel#READ_UNCOMMITTED}</li>
     * <li>{@link IsolationLevel#READ_COMMITTED}</li>
     * <li>{@link IsolationLevel#REPEATABLE_READ}</li>
     * <li>{@link IsolationLevel#SERIALIZABLE}</li>
     * </ul>
     * <p>
     * {@link IsolationLevel} is extensible so drivers can accept a vendor-specific {@link IsolationLevel}.
     *
     * @param isolationLevel the isolation level for this transaction
     * @return a {@link Publisher} that indicates that a transaction level has been configured
     * @throws IllegalArgumentException if {@code isolationLevel} is {@code null}
     */
    Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel);

    /**
     * Validates the connection according to the given {@link ValidationDepth}.
     * Emits {@literal true} if the validation was successful or {@literal false} if the validation failed. Does not emit errors and does not complete empty.
     *
     * @param depth the validation depth
     * @return a {@link Publisher} that indicates whether the validation was successful
     * @throws IllegalArgumentException if {@code depth} is {@code null}
     */
    Publisher<Boolean> validate(ValidationDepth depth);

}
