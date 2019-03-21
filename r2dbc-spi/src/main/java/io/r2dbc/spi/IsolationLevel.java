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
 * SQL transaction isolation levels.
 */
public enum IsolationLevel {

    /**
     * The read committed isolation level.
     */
    READ_COMMITTED("READ COMMITTED"),

    /**
     * The read uncommitted isolation level.
     */
    READ_UNCOMMITTED("READ UNCOMMITTED"),

    /**
     * The repeatable read isolation level.
     */
    REPEATABLE_READ("REPEATABLE READ"),

    /**
     * The serializable isolation level.
     */
    SERIALIZABLE("SERIALIZABLE");

    private final String sql;

    IsolationLevel(String sql) {
        this.sql = Assert.requireNonNull(sql, "sql must not be null");
    }

    /**
     * Returns the SQL string represented by each value.
     *
     * @return the SQL string represented by each value
     */
    public String asSql() {
        return this.sql;
    }

}

