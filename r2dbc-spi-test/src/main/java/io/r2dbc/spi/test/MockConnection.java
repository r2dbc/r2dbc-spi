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

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionMetadata;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.TransactionDefinition;
import io.r2dbc.spi.ValidationDepth;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public final class MockConnection implements Connection {

    private final MockBatch batch;

    private final MockStatement statement;

    private boolean autoCommit = true;

    private boolean beginTransactionCalled = false;

    private boolean closeCalled = false;

    private boolean commitTransactionCalled = false;

    private String createSavepointName;

    private String createStatementSql;

    private String releaseSavepointName;

    private boolean rollbackTransactionCalled = false;

    private String rollbackTransactionToSavepointName;

    private TransactionDefinition beginTransactionDefinition;

    private IsolationLevel setTransactionIsolationLevelIsolationLevel;

    private boolean valid;

    private boolean validateCalled;

    private ValidationDepth validationDepth;

    private MockConnection(@Nullable MockBatch batch, @Nullable MockStatement statement, boolean valid) {
        this.batch = batch;
        this.statement = statement;
        this.valid = valid;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MockConnection empty() {
        return builder().build();
    }

    @Override
    public Mono<Void> beginTransaction() {
        this.beginTransactionCalled = true;
        return Mono.empty();
    }

    @Override
    public Publisher<Void> beginTransaction(TransactionDefinition definition) {
        this.beginTransactionCalled = true;
        this.beginTransactionDefinition = definition;
        return Mono.empty();
    }

    @Override
    public Mono<Void> close() {
        this.closeCalled = true;
        return Mono.empty();
    }

    @Override
    public Mono<Void> commitTransaction() {
        this.commitTransactionCalled = true;
        return Mono.empty();
    }

    @Override
    public MockBatch createBatch() {
        if (this.batch == null) {
            throw new AssertionError("Unexpected call to createBatch()");
        }

        return this.batch;
    }

    @Override
    public Mono<Void> createSavepoint(String name) {
        this.createSavepointName = Assert.requireNonNull(name, "name must not be null");
        return Mono.empty();
    }

    @Override
    public MockStatement createStatement(String sql) {
        Assert.requireNonNull(sql, "sql must not be null");

        if (this.statement == null) {
            throw new AssertionError("Unexpected call to createStatement(String)");
        }

        this.createStatementSql = sql;
        return this.statement;
    }

    @Nullable
    public String getCreateSavepointName() {
        return this.createSavepointName;
    }

    @Nullable
    public String getCreateStatementSql() {
        return this.createStatementSql;
    }

    @Nullable
    public String getReleaseSavepointName() {
        return this.releaseSavepointName;
    }

    @Nullable
    public String getRollbackTransactionToSavepointName() {
        return this.rollbackTransactionToSavepointName;
    }

    @Override
    public ConnectionMetadata getMetadata() {
        return MockConnectionMetadata.INSTANCE;
    }

    public TransactionDefinition getBeginTransactionDefinition() {
        return this.beginTransactionDefinition;
    }

    @Nullable
    @Override
    public IsolationLevel getTransactionIsolationLevel() {
        return this.setTransactionIsolationLevelIsolationLevel;
    }

    @Nullable
    public ValidationDepth getValidationDepth() {
        return this.validationDepth;
    }

    @Override
    public boolean isAutoCommit() {
        return this.autoCommit;
    }

    public boolean isBeginTransactionCalled() {
        return this.beginTransactionCalled;
    }

    public boolean isCloseCalled() {
        return this.closeCalled;
    }

    public boolean isCommitTransactionCalled() {
        return this.commitTransactionCalled;
    }

    public boolean isRollbackTransactionCalled() {
        return this.rollbackTransactionCalled;
    }

    public boolean isValidateCalled() {
        return this.validateCalled;
    }

    @Override
    public Mono<Void> releaseSavepoint(String name) {
        this.releaseSavepointName = Assert.requireNonNull(name, "name must not be null");
        return Mono.empty();
    }

    @Override
    public Mono<Void> rollbackTransaction() {
        this.rollbackTransactionCalled = true;
        return Mono.empty();
    }

    @Override
    public Mono<Void> rollbackTransactionToSavepoint(String name) {
        this.rollbackTransactionToSavepointName = Assert.requireNonNull(name, "name must not be null");
        return Mono.empty();
    }

    @Override
    public Publisher<Void> setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
        return Mono.empty();
    }

    @Override
    public Mono<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) {
        this.setTransactionIsolationLevelIsolationLevel = Assert.requireNonNull(isolationLevel, "isolation level must not be null");
        return Mono.empty();
    }

    @Override
    public Publisher<Boolean> validate(ValidationDepth depth) {
        this.validationDepth = Assert.requireNonNull(depth, "depth must not be null");
        this.validateCalled = true;
        return Mono.just(this.valid);
    }

    @Override
    public String toString() {
        return "MockConnection{" +
            "batch=" + this.batch +
            ", statement=" + this.statement +
            ", beginTransactionCalled=" + this.beginTransactionCalled +
            ", closeCalled=" + this.closeCalled +
            ", commitTransactionCalled=" + this.commitTransactionCalled +
            ", createSavepointName='" + this.createSavepointName + '\'' +
            ", createStatementSql='" + this.createStatementSql + '\'' +
            ", releaseSavepointName='" + this.releaseSavepointName + '\'' +
            ", rollbackTransactionCalled=" + this.rollbackTransactionCalled +
            ", rollbackTransactionToSavepointName='" + this.rollbackTransactionToSavepointName + '\'' +
            ", setTransactionIsolationLevelIsolationLevel=" + this.setTransactionIsolationLevelIsolationLevel +
            ", valid=" + this.valid +
            ", validateCalled=" + this.validateCalled +
            ", validationDepth=" + this.validationDepth +
            '}';
    }

    public static final class Builder {

        private MockBatch batch;

        private MockStatement statement;

        private boolean valid;

        private Builder() {
        }

        public Builder batch(MockBatch batch) {
            this.batch = Assert.requireNonNull(batch, "batch must not be null");
            return this;
        }

        public MockConnection build() {
            return new MockConnection(this.batch, this.statement, this.valid);
        }

        public Builder statement(MockStatement statement) {
            this.statement = Assert.requireNonNull(statement, "statement must not be null");
            return this;
        }

        public Builder valid(boolean valid) {
            this.valid = valid;
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                "batch=" + this.batch +
                ", statement=" + this.statement +
                ", valid=" + this.valid +
                '}';
        }

    }

}
