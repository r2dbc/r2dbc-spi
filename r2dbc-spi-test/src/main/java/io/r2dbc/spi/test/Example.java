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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface Example<T> {

    static <T> Mono<T> close(Connection connection) {
        return Mono.from(connection
            .close())
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
    default void blobInsert() {
        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Flux.from(connection

                .createStatement(String.format("INSERT INTO blob_test VALUES (%s)", getPlaceholder(0)))
                .bind(getPlaceholder(0), Blob.from(Mono.just(ByteBuffer.wrap("abc".getBytes()))))
                .execute())

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    default void blobSelect() {
        blobInsert();

        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Flux.from(connection

                .createStatement("SELECT * from blob_test")
                .execute())
                .flatMap(result -> result
                    .map((row, rowMetadata) -> row.get("image", Blob.class)))
                .flatMap(Blob::stream)
                .map(ByteBuffer::array)

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNextMatches(bytes -> {
                assertEquals(54, bytes[0]);
                assertEquals(49, bytes[1]);
                assertEquals(54, bytes[2]);
                assertEquals(50, bytes[3]);
                assertEquals(54, bytes[4]);
                assertEquals(51, bytes[5]);

                return true;
            })
            .verifyComplete();
    }

    @Test
    default void clobInsert() {
        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Flux.from(connection

                .createStatement(String.format("INSERT INTO clob_test VALUES (%s)", getPlaceholder(0)))
                .bind(getPlaceholder(0), Clob.from(Mono.just("abcd")))
                .execute())

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    default void clobSelect() {
        clobInsert();

        Mono.from(getConnectionFactory().create())
            .flatMapMany(connection -> Flux.from(connection

                .createStatement("SELECT * from clob_test")
                .execute())
                .flatMap(result -> result
                    .map((row, rowMetadata) -> row.get("content", Clob.class)))
                .flatMap(Clob::stream)

                .concatWith(close(connection)))
            .as(StepVerifier::create)
            .expectNextMatches(charSequence -> {
                assertEquals("abcd", charSequence.toString());
                return true;
            })
            .verifyComplete();
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

    @BeforeEach
    default void createTable() {
        getJdbcOperations().execute("CREATE TABLE test ( value INTEGER )");
        getJdbcOperations().execute("CREATE TABLE blob_test ( image BLOB )");
        getJdbcOperations().execute("CREATE TABLE clob_test ( content CLOB )");
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
