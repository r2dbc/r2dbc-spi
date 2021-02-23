/*
 * Copyright 2021 the original author or authors.
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
 * Exception thrown by various accessor methods to indicate that the {@link Option} being requested
 * does not exist or is not configured.
 *
 * @since 0.9
 */
public class NoSuchOptionException extends IllegalStateException {

    private final Option<?> option;

    /**
     * Creates a new {@link NoSuchOptionException}.
     *
     * @param reason the reason for the error.  Set as the exception's message and retrieved with {@link #getMessage()}.
     * @param option the requested {@link Option}
     */
    public NoSuchOptionException(String reason, Option<?> option) {
        super(reason);
        this.option = option;
    }

    public Option<?> getOption() {
        return this.option;
    }

}
