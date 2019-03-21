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
 * A root exception that should be extended by all server-related exceptions in an implementation.
 */
public abstract class R2dbcException extends RuntimeException {

    private final int errorCode;

    private final String sqlState;

    /**
     * Creates a new exception.
     */
    public R2dbcException() {
        this((String) null);
    }

    /**
     * Creates a new exception.
     *
     * @param reason the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     */
    public R2dbcException(@Nullable String reason) {
        this(reason, (String) null);
    }

    /**
     * Creates a new exception.
     *
     * @param reason   the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState the "SQLstate" string, which follows either the XOPEN SQLstate conventions or the SQL:2003 conventions
     */
    public R2dbcException(@Nullable String reason, @Nullable String sqlState) {
        this(reason, sqlState, 0);
    }

    /**
     * Creates a new exception.
     *
     * @param reason    the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLstate" string, which follows either the XOPEN SQLstate conventions or the SQL:2003 conventions
     * @param errorCode a vendor-specific error code representing this failure
     */
    public R2dbcException(@Nullable String reason, @Nullable String sqlState, int errorCode) {
        this(reason, sqlState, errorCode, null);
    }

    /**
     * Creates a new exception.
     *
     * @param reason    the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLstate" string, which follows either the XOPEN SQLstate conventions or the SQL:2003 conventions
     * @param errorCode a vendor-specific error code representing this failure
     * @param cause     the cause
     */
    public R2dbcException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable Throwable cause) {
        super(reason, cause);
        this.sqlState = sqlState;
        this.errorCode = errorCode;
    }

    /**
     * Creates a new exception.
     *
     * @param reason   the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState the "SQLstate" string, which follows either the XOPEN SQLstate conventions or the SQL:2003 conventions
     * @param cause    the cause
     */
    public R2dbcException(@Nullable String reason, @Nullable String sqlState, @Nullable Throwable cause) {
        this(reason, sqlState, 0, cause);
    }

    /**
     * Creates a new exception.
     *
     * @param reason the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param cause  the cause
     */
    public R2dbcException(@Nullable String reason, @Nullable Throwable cause) {
        this(reason, null, cause);
    }

    /**
     * Creates a new exception.
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
        return errorCode;
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

    @Override
    public String toString() {
        return "R2dbcException{" +
            "errorCode=" + errorCode +
            ", sqlState='" + sqlState + '\'' +
            "} " + super.toString();
    }
}
