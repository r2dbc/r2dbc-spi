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
 * Exception thrown when the SQL statement has a problem in its syntax.
 */
public class R2dbcBadGrammarException extends R2dbcNonTransientException {

    private final String offendingSql;

    /**
     * Creates a new {@link R2dbcBadGrammarException}.
     */
    public R2dbcBadGrammarException() {
        super();
        this.offendingSql = null;
    }

    /**
     * Creates a new {@link R2dbcBadGrammarException}.
     *
     * @param reason the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     */
    public R2dbcBadGrammarException(@Nullable String reason) {
        super(reason);
        this.offendingSql = null;
    }

    /**
     * Creates a new {@link R2dbcBadGrammarException}.
     *
     * @param reason   the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                 conventions
     */
    public R2dbcBadGrammarException(@Nullable String reason, @Nullable String sqlState) {
        super(reason, sqlState);
        this.offendingSql = null;
    }

    /**
     * Creates a new {@link R2dbcBadGrammarException}.
     *
     * @param reason    the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                  conventions
     * @param errorCode a vendor-specific error code representing this failure
     */
    public R2dbcBadGrammarException(@Nullable String reason, @Nullable String sqlState, int errorCode) {
        super(reason, sqlState, errorCode);
        this.offendingSql = null;
    }

    /**
     * Creates a new {@link R2dbcBadGrammarException}.
     *
     * @param reason    the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                  conventions
     * @param errorCode a vendor-specific error code representing this failure
     * @param offendingSql the SQL statement that caused this error
     */
    public R2dbcBadGrammarException(@Nullable String reason, @Nullable String sqlState, int errorCode,
                                    @Nullable String offendingSql) {
        super(reason, sqlState, errorCode);
        this.offendingSql = offendingSql;
    }

    /**
     * Creates a new {@link R2dbcBadGrammarException}.
     *
     * @param reason    the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                  conventions
     * @param errorCode a vendor-specific error code representing this failure
     * @param cause     the cause
     */
    public R2dbcBadGrammarException(@Nullable String reason, @Nullable String sqlState, int errorCode,
                                    @Nullable Throwable cause) {
        super(reason, sqlState, errorCode, cause);
        this.offendingSql = null;
    }

    /**
     * Creates a new {@link R2dbcBadGrammarException}.
     *
     * @param reason    the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState  the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                  conventions
     * @param errorCode a vendor-specific error code representing this failure
     * @param offendingSql the SQL statement that caused this error
     * @param cause     the cause
     */
    public R2dbcBadGrammarException(@Nullable String reason, @Nullable String sqlState, int errorCode,
                                    @Nullable String offendingSql, @Nullable Throwable cause) {
        super(reason, sqlState, errorCode, cause);
        this.offendingSql = offendingSql;
    }

    /**
     * Creates a new {@link R2dbcBadGrammarException}.
     *
     * @param reason   the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param sqlState the "SQLState" string, which follows either the XOPEN SQLState conventions or the SQL:2003
     *                 conventions
     * @param cause    the cause
     */
    public R2dbcBadGrammarException(@Nullable String reason, @Nullable String sqlState, @Nullable Throwable cause) {
        super(reason, sqlState, cause);
        this.offendingSql = null;
    }

    /**
     * Creates a new {@link R2dbcBadGrammarException}.
     *
     * @param reason the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param cause  the cause
     */
    public R2dbcBadGrammarException(@Nullable String reason, @Nullable Throwable cause) {
        super(reason, cause);
        this.offendingSql = null;
    }

    /**
     * Creates a new {@link R2dbcBadGrammarException}.
     *
     * @param cause the cause
     */
    public R2dbcBadGrammarException(@Nullable Throwable cause) {
        super(cause);
        this.offendingSql = null;
    }

    /**
     * Returns the offending SQL String.
     *
     * @return offendingSql
     */
    @Nullable
    public String getOffendingSql() {
        return this.offendingSql;
    }
}
