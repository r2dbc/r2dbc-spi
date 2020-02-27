/*
 * Copyright 2017-2020 the original author or authors.
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
 * Represents a transaction isolation level constant.
 */
public final class IsolationLevel implements TransactionDefinition {

    private static final ConstantPool<IsolationLevel> CONSTANTS = new ConstantPool<IsolationLevel>() {

        @Override
        IsolationLevel createConstant(String name, boolean sensitive) {
            return new IsolationLevel(name);
        }

    };

    /**
     * The read committed isolation level.
     */
    public static final IsolationLevel READ_COMMITTED = IsolationLevel.valueOf("READ COMMITTED");

    /**
     * The read uncommitted isolation level.
     */
    public static final IsolationLevel READ_UNCOMMITTED = IsolationLevel.valueOf("READ UNCOMMITTED");

    /**
     * The repeatable read isolation level.
     */
    public static final IsolationLevel REPEATABLE_READ = IsolationLevel.valueOf("REPEATABLE READ");

    /**
     * The serializable isolation level.
     */
    public static final IsolationLevel SERIALIZABLE = IsolationLevel.valueOf("SERIALIZABLE");

    private final String sql;

    private IsolationLevel(String sql) {
        this.sql = Assert.requireNonNull(sql, "sql must not be null");
    }

    /**
     * Returns a constant single of an isolation level.
     *
     * @param sql the SQL statement representing the isolation level
     * @return a constant singleton of the the isolation level
     * @throws IllegalArgumentException if {@code name} is {@code null} or empty
     */
    public static IsolationLevel valueOf(String sql) {
        Assert.requireNonNull(sql, "sql must not be null");
        Assert.requireNonEmpty(sql, "sql must not be empty");

        return CONSTANTS.valueOf(sql, false);
    }

    @Override
    public <T> T getAttribute(String name, Class<T> type) {
        Assert.requireNonNull(name, "sql must not be null");
        Assert.requireNonNull(type, "type must not be null");

        if (name.equals(ISOLATION_LEVEL)) {
            return type.cast(this);
        }

        return null;
    }

    /**
     * Returns the SQL string represented by each value.
     *
     * @return the SQL string represented by each value
     */
    public String asSql() {
        return this.sql;
    }

    @Override
    public String toString() {
        return "IsolationLevel{" +
            "sql='" + this.sql + '\'' +
            '}';
    }

}
