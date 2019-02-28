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
 * Exception thrown when an attempt to insert or update data results in a violation of an integrity constraint.
 */
public class R2dbcDataIntegrityViolationException extends R2dbcNonTransientException {

    /**
     * Creates a new {@link R2dbcDataIntegrityViolationException}.
     */
    public R2dbcDataIntegrityViolationException() {
        super();
    }

    /**
     * Creates a new {@link R2dbcDataIntegrityViolationException}.
     *
     * @param reason the reason for the error. Set as the exception's message and retrieved with {@link #getMessage()}.
     */
    public R2dbcDataIntegrityViolationException(@Nullable String reason) {
        super(reason);
    }

    /**
     * Creates a new {@link R2dbcDataIntegrityViolationException}.
     *
     * @param reason   the reason for the error. Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                 conventions
     */
    public R2dbcDataIntegrityViolationException(@Nullable String reason, @Nullable String sqlState) {
        super(reason, sqlState);
    }

    /**
     * Creates a new {@link R2dbcDataIntegrityViolationException}.
     *
     * @param reason    the reason for the error. Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                  conventions
     * @param errorCode a vendor-specific error code representing this failure
     */
    public R2dbcDataIntegrityViolationException(@Nullable String reason, @Nullable String sqlState, int errorCode) {
        super(reason, sqlState, errorCode);
    }

    /**
     * Creates a new {@link R2dbcDataIntegrityViolationException}.
     *
     * @param reason    the reason for the error. Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                  conventions
     * @param errorCode a vendor-specific error code representing this failure
     * @param cause     the cause
     */
    public R2dbcDataIntegrityViolationException(@Nullable String reason, @Nullable String sqlState, int errorCode,
                                                @Nullable Throwable cause) {
        super(reason, sqlState, errorCode, cause);
    }

    /**
     * Creates a new {@link R2dbcDataIntegrityViolationException}.
     *
     * @param reason   the reason for the error. Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                 conventions
     * @param cause    the cause
     */
    public R2dbcDataIntegrityViolationException(@Nullable String reason, @Nullable String sqlState,
                                                @Nullable Throwable cause) {
        super(reason, sqlState, cause);
    }

    /**
     * Creates a new {@link R2dbcDataIntegrityViolationException}.
     *
     * @param reason the reason for the error. Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param cause  the cause
     */
    public R2dbcDataIntegrityViolationException(@Nullable String reason, @Nullable Throwable cause) {
        super(reason, cause);
    }

    /**
     * Creates a new {@link R2dbcDataIntegrityViolationException}.
     *
     * @param cause the cause
     */
    public R2dbcDataIntegrityViolationException(@Nullable Throwable cause) {
        super(cause);
    }
}
