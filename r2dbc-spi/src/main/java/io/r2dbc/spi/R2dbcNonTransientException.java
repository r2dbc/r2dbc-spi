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
 * Base exception thrown when a retry of the same operation would fail unless the cause of the is corrected.
 *
 * @see R2dbcBadGrammarException
 * @see R2dbcDataIntegrityViolationException
 * @see R2dbcPermissionDeniedException
 * @see R2dbcNonTransientResourceException
 */
public abstract class R2dbcNonTransientException extends R2dbcException {

    /**
     * Creates a new {@link R2dbcNonTransientException}.
     */
    public R2dbcNonTransientException() {
        super();
    }

    /**
     * Creates a new {@link R2dbcNonTransientException}.
     *
     * @param reason the reason for the error. Set as the exception's message and retrieved with {@link #getMessage()}.
     */
    public R2dbcNonTransientException(@Nullable String reason) {
        super(reason);
    }

    /**
     * Creates a new {@link R2dbcNonTransientException}.
     *
     * @param reason   the reason for the error. Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                 conventions
     */
    public R2dbcNonTransientException(@Nullable String reason, @Nullable String sqlState) {
        super(reason, sqlState);
    }

    /**
     * Creates a new {@link R2dbcNonTransientException}.
     *
     * @param reason    the reason for the error. Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                  conventions
     * @param errorCode a vendor-specific error code representing this failure
     */
    public R2dbcNonTransientException(@Nullable String reason, @Nullable String sqlState, int errorCode) {
        super(reason, sqlState, errorCode);
    }

    /**
     * Creates a new {@link R2dbcNonTransientException}.
     *
     * @param reason    the reason for the error. Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                  conventions
     * @param errorCode a vendor-specific error code representing this failure
     * @param cause     the cause
     */
    public R2dbcNonTransientException(@Nullable String reason, @Nullable String sqlState, int errorCode,
                                      @Nullable Throwable cause) {
        super(reason, sqlState, errorCode, cause);
    }

    /**
     * Creates a new {@link R2dbcNonTransientException}.
     *
     * @param reason   the reason for the error. Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                 conventions
     * @param cause    the cause
     */
    public R2dbcNonTransientException(@Nullable String reason, @Nullable String sqlState, @Nullable Throwable cause) {
        super(reason, sqlState, cause);
    }

    /**
     * Creates a new {@link R2dbcNonTransientException}.
     *
     * @param reason the reason for the error. Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param cause  the cause
     */
    public R2dbcNonTransientException(@Nullable String reason, @Nullable Throwable cause) {
        super(reason, cause);
    }

    /**
     * Creates a new {@link R2dbcNonTransientException}.
     *
     * @param cause the cause
     */
    public R2dbcNonTransientException(@Nullable Throwable cause) {
        super(cause);
    }
}
