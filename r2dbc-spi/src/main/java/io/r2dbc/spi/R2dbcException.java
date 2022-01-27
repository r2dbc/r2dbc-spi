/*
 * Copyright 2017-2021 the original author or authors.
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
 * A root exception that should be extended by all server-related exceptions in an implementation.
 */
public abstract class R2dbcException extends RuntimeException {

    private final int errorCode;

    @Nullable
    private final String sqlState;

    @Nullable
    private final String sql;

    /**
     * Creates a new {@link R2dbcException}.
     */
    public R2dbcException() {
        this((String) null);
    }

    /**
     * Creates a new {@link R2dbcException}.
     *
     * @param reason the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     */
    public R2dbcException(@Nullable String reason) {
        this(reason, (String) null);
    }

    /**
     * Creates a new {@link R2dbcException}.
     *
     * @param reason   the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                 conventions
     */
    public R2dbcException(@Nullable String reason, @Nullable String sqlState) {
        this(reason, sqlState, 0);
    }

    /**
     * Creates a new {@link R2dbcException}.
     *
     * @param reason    the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                  conventions
     * @param errorCode a vendor-specific error code representing this failure
     */
    public R2dbcException(@Nullable String reason, @Nullable String sqlState, int errorCode) {
        this(reason, sqlState, errorCode, null, null);
    }

    /**
     * Creates a new {@link R2dbcException}.
     *
     * @param reason    the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                  conventions
     * @param errorCode a vendor-specific error code representing this failure
     * @param sql       the SQL statement that caused this error
     * @since 0.9
     */
    public R2dbcException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable String sql) {
        this(reason, sqlState, errorCode, sql, null);
    }

    /**
     * Creates a new {@link R2dbcException}.
     *
     * @param reason    the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                  conventions
     * @param errorCode a vendor-specific error code representing this failure
     * @param sql       the SQL statement that caused this error
     * @param cause     the cause
     * @since 0.9
     */
    public R2dbcException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable String sql, @Nullable Throwable cause) {
        super(reason, cause);
        this.sqlState = sqlState;
        this.errorCode = errorCode;
        this.sql = sql;
    }

    /**
     * Creates a new {@link R2dbcException}.
     *
     * @param reason    the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                  conventions
     * @param errorCode a vendor-specific error code representing this failure
     * @param cause     the cause
     */
    public R2dbcException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable Throwable cause) {
        this(reason, sqlState, errorCode, null, cause);
    }

    /**
     * Creates a new {@link R2dbcException}.
     *
     * @param reason   the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                 conventions
     * @param cause    the cause
     */
    public R2dbcException(@Nullable String reason, @Nullable String sqlState, @Nullable Throwable cause) {
        this(reason, sqlState, 0, cause);
    }

    /**
     * Creates a new {@link R2dbcException}.
     *
     * @param reason the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param cause  the cause
     */
    public R2dbcException(@Nullable String reason, @Nullable Throwable cause) {
        this(reason, null, cause);
    }

    /**
     * Creates a new {@link R2dbcException}.
     *
     * @param cause the cause
     */
    public R2dbcException(@Nullable Throwable cause) {
        this(null, cause);
    }

    /**
     * Returns the vendor-specific error code.
     *
     * @return the vendor-specific error code
     */
    public final int getErrorCode() {
        return this.errorCode;
    }

    /**
     * Returns the SQLState.
     *
     * @return the SQLState
     */
    @Nullable
    public final String getSqlState() {
        return this.sqlState;
    }

    /**
     * Returns the SQL that led to the problem (if known).
     *
     * @return the SQL
     * @since 0.9
     */
    @Nullable
    public final String getSql() {
        return this.sql;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder(32);
        builder.append(getClass().getName());

        if (getErrorCode() != 0 || (getSqlState() != null && !getSqlState()
            .isEmpty()) || getMessage() != null) {
            builder.append(":");
        }

        if (getErrorCode() != 0) {
            builder.append(" [").append(getErrorCode()).append("]");
        }

        if (getSqlState() != null && !getSqlState().isEmpty()) {
            builder.append(" [").append(getSqlState()).append("]");
        }

        if (getMessage() != null) {
            builder.append(" ").append(getMessage());
        }

        return builder.toString();
    }

}
