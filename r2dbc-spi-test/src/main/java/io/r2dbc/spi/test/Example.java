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

package io.r2dbc.spi.test;

import io.r2dbc.spi.Blob;
import io.r2dbc.spi.Clob;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public interface Example<T> {

    static <T> Mono<T> close(Connection connection) {
        return Mono.from(connection
            .close())
            .then(Mono.empty());
    }

    static <T> Mono<T> discard(Blob blob) {
        return Mono.from(blob
            .discard())
            .then(Mono.empty());
    }

    static <T> Mono<T> discard(Clob clob) {
        return Mono.from(clob
            .discard())
            .then(Mono.empty());
    }

    static Mono<List<Integer>> extractColumns(Result result) {
        return Flux.from(result
            .map((row, rowMetadata) -> row.get("value", Integer.class)))
            .collectList();
    }

    static Mono<Integer> extractRowsUpdated(Result result) {
        return Mono.from(result.getRowsUpdated());
    }

    @Test
    default void batch() {
        getJdbcOperations().execute("INSERT INTO test VALUES (100)");

        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Flux.from(connection

                .createBatch()
                .add("INSERT INTO test VALUES(200)")
                .add("SELECT value FROM test")
                .execute())

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNextCount(2).as("one result for each statement")
            .verifyComplete();
    }

    @Test
    default void bindNull() {
        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Flux.from(connection

                .createStatement(String.format("INSERT INTO test VALUES(%s)", getPlaceholder(0)))
                .bindNull(getIdentifier(0), Integer.class).add()
                .execute())

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNextCount(1).as("rows inserted")
            .verifyComplete();
    }

    @Test
    default void bindNullFails() {
        Mono.from(getConnectionFactory().create())
            .flatMap(connection -> {

                Statement statement = connection.createStatement(String.format("INSERT INTO test VALUES(%s)", getPlaceholder(0)));
                assertThrows(IllegalArgumentException.class, () -> statement.bindNull(null, String.class), "bindNull(null, …) should fail");
                assertThrows(IllegalArgumentException.class, () -> statement.bindNull(getIdentifier(0), null), "bindNull(identifier, null) should fail");
                return close(connection);
            })
            .as(StepVerifier::create)
            .verifyComplete();
    }

    @Test
    default void bindFails() {
        Mono.from(getConnectionFactory().create())
            .flatMap(connection -> {

                Statement statement = connection.createStatement(String.format("INSERT INTO test VALUES(%s)", getPlaceholder(0)));
                assertThrows(IllegalArgumentException.class, () -> statement.bind(0, null), "bind(0, null) should fail");
                assertThrows(IndexOutOfBoundsException.class, () -> statement.bind(99, ""), "bind(nonexistent-index, null) should fail");
                assertThrows(IllegalArgumentException.class, () -> statement.bind(getIdentifier(0), null), "bind(identifier, null) should fail");
                assertThrows(IllegalArgumentException.class, () -> statement.bind(getIdentifier(0), Class.class), "bind(identifier, Class.class) should fail");
                assertThrows(IllegalArgumentException.class, () -> statement.bind("unknown", ""), "bind(unknown-placeholder, \"\") should fail");
                return close(connection);
            })
            .as(StepVerifier::create)
            .verifyComplete();
    }

    @Test
    default void blobInsert() {
        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Flux.from(connection

                .createStatement(String.format("INSERT INTO blob_test VALUES (%s)", getPlaceholder(0)))
                .bind(getPlaceholder(0), Blob.from(Mono.just(StandardCharsets.UTF_8.encode("test-value"))))
                .execute())

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNextCount(1).as("rows inserted")
            .verifyComplete();
    }

    @Test
    default void blobSelect() {
        getJdbcOperations().execute("INSERT INTO blob_test VALUES (?)", new AbstractLobCreatingPreparedStatementCallback(new DefaultLobHandler()) {

            @Override
            protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
                lobCreator.setBlobAsBytes(ps, 1, StandardCharsets.UTF_8.encode("test-value").array());
            }

        });

        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Flux.from(connection

                .createStatement("SELECT * from blob_test")
                .execute())
                .flatMap(result -> result
                    .map((row, rowMetadata) -> row.get("value", Blob.class)))
                .flatMap(blob -> Flux.from(blob.stream())
                    .reduce(ByteBuffer::put)
                    .concatWith(discard(blob)))

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNextMatches(actual -> {
                ByteBuffer expected = StandardCharsets.UTF_8.encode("test-value");
                return Arrays.equals(expected.array(), actual.array());
            })
            .verifyComplete();
    }

    default String blobType() {
        return "BLOB";
    }

    @Test
    default void clobInsert() {
        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Flux.from(connection

                .createStatement(String.format("INSERT INTO clob_test VALUES (%s)", getPlaceholder(0)))
                .bind(getPlaceholder(0), Clob.from(Mono.just("test-value")))
                .execute())

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNextCount(1).as("rows inserted")
            .verifyComplete();
    }

    @Test
    default void clobSelect() {
        getJdbcOperations().execute("INSERT INTO clob_test VALUES (?)", new AbstractLobCreatingPreparedStatementCallback(new DefaultLobHandler()) {

            @Override
            protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
                lobCreator.setClobAsString(ps, 1, "test-value");
            }

        });

        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Flux.from(connection

                .createStatement("SELECT * from clob_test")
                .execute())
                .flatMap(result -> result
                    .map((row, rowMetadata) -> row.get("value", Clob.class)))
                .flatMap(clob -> Flux.from(clob.stream())
                    .reduce(new StringBuilder(), StringBuilder::append)
                    .map(StringBuilder::toString)
                    .concatWith(discard(clob)))

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNext("test-value").as("value from select")
            .verifyComplete();
    }

    default String clobType() {
        return "CLOB";
    }

    @Test
    default void compoundStatement() {
        getJdbcOperations().execute("INSERT INTO test VALUES (100)");

        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Flux.from(connection

                .createStatement("SELECT value FROM test; SELECT value FROM test")
                .execute())
                .flatMap(Example::extractColumns)

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNext(Collections.singletonList(100)).as("value from first select")
            .expectNext(Collections.singletonList(100)).as("value from second select")
            .verifyComplete();
    }

    @Test
    default void createStatementFails() {

        Mono.from(getConnectionFactory().create())
            .flatMap(connection -> {
                assertThrows(IllegalArgumentException.class, () -> connection.createStatement(null));

                return close(connection);
            })
            .as(StepVerifier::create)
            .verifyComplete();
    }

    @BeforeEach
    default void createTable() {
        getJdbcOperations().execute("CREATE TABLE test ( value INTEGER )");
        getJdbcOperations().execute(String.format("CREATE TABLE blob_test ( value %s )", blobType()));
        getJdbcOperations().execute(String.format("CREATE TABLE clob_test ( value %s )", clobType()));
    }

    @AfterEach
    default void dropTable() {
        getJdbcOperations().execute("DROP TABLE test");
        getJdbcOperations().execute("DROP TABLE blob_test");
        getJdbcOperations().execute("DROP TABLE clob_test");
    }

    /**
     * Returns a {@link ConnectionFactory} for the connected database.
     *
     * @return a {@link ConnectionFactory} for the connected database
     */
    ConnectionFactory getConnectionFactory();

    /**
     * Returns the bind identifier for a given substitution.
     *
     * @param index the zero-index number of the substitution
     * @return the bind identifier for a given substitution
     */
    T getIdentifier(int index);

    /**
     * Returns a {@link JdbcOperations} for the connected database.
     *
     * @return a {@link JdbcOperations} for the connected database
     */
    JdbcOperations getJdbcOperations();

    /**
     * Returns the {@code CREATE TABLE} statement for a table named {@code test}
     * with two columns: First one uses auto-generated keys, second one is named {@code value} of type {@code INT}.
     * <p>
     * Example:
     * <pre class="code">
     *     CREATE TABLE test ( id SERIAL,  value INTEGER );
     *  // or
     *     CREATE TABLE test ( id INTEGER IDENTITY,  value INTEGER );
     *     </pre>
     *
     * @return the {@code CREATE TABLE} statement
     */
    String getCreateTableWithAutogeneratedKey();

    /**
     * Returns the database-specific placeholder for a given substitution.
     *
     * @param index the zero-index number of the substitution
     * @return the database-specific placeholder for a given substitution
     */
    String getPlaceholder(int index);

    @Test
    default void prepareStatement() {
        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> {
                Statement statement = connection.createStatement(String.format("INSERT INTO test VALUES(%s)", getPlaceholder(0)));

                IntStream.range(0, 10)
                    .forEach(i -> statement.bind(getIdentifier(0), i).add());

                return Flux.from(statement
                    .execute())
                    .concatWith(close(connection));
            })
            .as(StepVerifier::create)
            .expectNextCount(10).as("values from insertions")
            .verifyComplete();
    }

    @Test
    default void prepareStatementWithIncompleteBatchFails() {
        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> {
                Statement statement = connection.createStatement(String.format("INSERT INTO test VALUES(%s,%s)", getPlaceholder(0), getPlaceholder(1)));

                statement.bind(getIdentifier(0), 0);

                assertThrows(IllegalStateException.class, statement::add);
                return close(connection);
            })
            .as(StepVerifier::create)
            .verifyComplete();
    }

    @Test
    default void prepareStatementWithIncompleteBindingFails() {
        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> {
                Statement statement = connection.createStatement(String.format("INSERT INTO test VALUES(%s,%s)", getPlaceholder(0), getPlaceholder(1)));

                statement.bind(getIdentifier(0), 0);

                assertThrows(IllegalStateException.class, statement::execute);
                return close(connection);
            })
            .as(StepVerifier::create)
            .verifyComplete();
    }

    @Test
    default void returnGeneratedValues() {

        getJdbcOperations().execute("DROP TABLE test");
        getJdbcOperations().execute(getCreateTableWithAutogeneratedKey());

        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> {
                Statement statement = connection.createStatement("INSERT INTO test VALUES(100)");

                statement.returnGeneratedValues();

                return Flux.from(statement
                    .execute())
                    .concatWith(close(connection)).flatMap(it -> it.map((row, rowMetadata) -> row.get(0)));
            })
            .as(StepVerifier::create)
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    default void returnGeneratedValuesFails() {

        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> {
                Statement statement = connection.createStatement("INSERT INTO test");

                assertThrows(IllegalArgumentException.class, () -> statement.returnGeneratedValues((String[]) null));
                return close(connection);
            })
            .as(StepVerifier::create)
            .verifyComplete();
    }

    @Test
    default void savePoint() {
        getJdbcOperations().execute("INSERT INTO test VALUES (100)");

        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Mono.from(connection

                .beginTransaction())
                .<Object>thenMany(Flux.from(connection.createStatement("SELECT value FROM test")
                    .execute())
                    .flatMap(Example::extractColumns))

                .concatWith(Flux.from(connection.createStatement(String.format("INSERT INTO test VALUES (%s)", getPlaceholder(0)))
                    .bind(getIdentifier(0), 200)
                    .execute())
                    .flatMap(Example::extractRowsUpdated))
                .concatWith(Flux.from(connection.createStatement("SELECT value FROM test")
                    .execute())
                    .flatMap(Example::extractColumns))

                .concatWith(connection.createSavepoint("test_savepoint"))
                .concatWith(Flux.from(connection.createStatement(String.format("INSERT INTO test VALUES (%s)", getPlaceholder(0)))
                    .bind(getIdentifier(0), 300)
                    .execute())
                    .flatMap(Example::extractRowsUpdated))
                .concatWith(Flux.from(connection.createStatement("SELECT value FROM test")
                    .execute())
                    .flatMap(Example::extractColumns))

                .concatWith(connection.rollbackTransactionToSavepoint("test_savepoint"))
                .concatWith(Flux.from(connection.createStatement("SELECT value FROM test")
                    .execute())
                    .flatMap(Example::extractColumns))

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNext(Collections.singletonList(100)).as("value from select")
            .expectNext(1).as("rows inserted")
            .expectNext(Arrays.asList(100, 200)).as("values from select")
            .expectNext(1).as("rows inserted")
            .expectNext(Arrays.asList(100, 200, 300)).as("values from select")
            .expectNext(Arrays.asList(100, 200)).as("values from select")
            .verifyComplete();
    }

    @Test
    default void transactionCommit() {
        getJdbcOperations().execute("INSERT INTO test VALUES (100)");

        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Mono.from(connection

                .beginTransaction())
                .<Object>thenMany(Flux.from(connection.createStatement("SELECT value FROM test")
                    .execute())
                    .flatMap(Example::extractColumns))

                .concatWith(Flux.from(connection.createStatement(String.format("INSERT INTO test VALUES (%s)", getPlaceholder(0)))
                    .bind(getIdentifier(0), 200)
                    .execute())
                    .flatMap(Example::extractRowsUpdated))
                .concatWith(Flux.from(connection.createStatement("SELECT value FROM test")
                    .execute())
                    .flatMap(Example::extractColumns))

                .concatWith(connection.commitTransaction())
                .concatWith(Flux.from(connection.createStatement("SELECT value FROM test")
                    .execute())
                    .flatMap(Example::extractColumns))

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNext(Collections.singletonList(100)).as("value from select")
            .expectNext(1).as("rows inserted")
            .expectNext(Arrays.asList(100, 200)).as("values from select")
            .expectNext(Arrays.asList(100, 200)).as("values from select")
            .verifyComplete();
    }

    @Test
    default void transactionRollback() {
        getJdbcOperations().execute("INSERT INTO test VALUES (100)");

        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Mono.from(connection

                .beginTransaction())
                .<Object>thenMany(Flux.from(connection.createStatement("SELECT value FROM test")
                    .execute())
                    .flatMap(Example::extractColumns))

                .concatWith(Flux.from(connection.createStatement(String.format("INSERT INTO test VALUES (%s)", getPlaceholder(0)))
                    .bind(getIdentifier(0), 200)
                    .execute())
                    .flatMap(Example::extractRowsUpdated))
                .concatWith(Flux.from(connection.createStatement("SELECT value FROM test")
                    .execute())
                    .flatMap(Example::extractColumns))

                .concatWith(connection.rollbackTransaction())
                .concatWith(Flux.from(connection.createStatement("SELECT value FROM test")
                    .execute())
                    .flatMap(Example::extractColumns))

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNext(Collections.singletonList(100)).as("value from select")
            .expectNext(1).as("rows inserted")
            .expectNext(Arrays.asList(100, 200)).as("values from select")
            .expectNext(Collections.singletonList(100)).as("value from select")
            .verifyComplete();
    }

}
