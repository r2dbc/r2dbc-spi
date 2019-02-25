/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.r2dbc.spi;

/**
 *  Will be thrown when the current SQL statement was rolled back by the database.
 *
 *  Some cases when this could happen: Deadlock, transaction serialization failures, etc.
 */
public class R2dbcRollbackException extends R2dbcTransientException {

    /**
     * Creates a new exception.
     */
    public R2dbcRollbackException() {
        super();
    }

    /**
     * Creates a new exception.
     *
     * @param reason the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     */
    public R2dbcRollbackException(@Nullable String reason) {
        super(reason);
    }

    /**
     * Creates a new exception.
     *
     * @param reason   the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState the "SQLstate" string, which follows either the XOPEN SQLstate conventions or the SQL:2003 conventions
     */
    public R2dbcRollbackException(@Nullable String reason, @Nullable String sqlState) {
        super(reason, sqlState);
    }

    /**
     * Creates a new exception.
     *
     * @param reason    the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLstate" string, which follows either the XOPEN SQLstate conventions or the SQL:2003 conventions
     * @param errorCode a vendor-specific error code representing this failure
     */
    public R2dbcRollbackException(@Nullable String reason, @Nullable String sqlState, int errorCode) {
        super(reason, sqlState, errorCode);
    }

    /**
     * Creates a new exception.
     *
     * @param reason    the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLstate" string, which follows either the XOPEN SQLstate conventions or the SQL:2003 conventions
     * @param errorCode a vendor-specific error code representing this failure
     * @param cause     the cause
     */
    public R2dbcRollbackException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable Throwable cause) {
        super(reason, sqlState, errorCode, cause);
    }

    /**
     * Creates a new exception.
     *
     * @param reason   the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState the "SQLstate" string, which follows either the XOPEN SQLstate conventions or the SQL:2003 conventions
     * @param cause    the cause
     */
    public R2dbcRollbackException(@Nullable String reason, @Nullable String sqlState, @Nullable Throwable cause) {
        super(reason, sqlState, cause);
    }

    /**
     * Creates a new exception.
     *
     * @param reason the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param cause  the cause
     */
    public R2dbcRollbackException(@Nullable String reason, @Nullable Throwable cause) {
        super(reason, cause);
    }

    /**
     * Creates a new exception.
     *
     * @param cause the cause
     */
    public R2dbcRollbackException(@Nullable Throwable cause) {
        super(cause);
    }
}
