/*
 * Copyright 2019-2022 the original author or authors.
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
 * Constants indicating validation depth for a {@link Connection}.
 */
public enum ValidationDepth {

    /**
     * Perform a client-side only validation.  Typically to determine whether a connection is still active or other mechanism that does not involve remote communication.
     */
    LOCAL,

    /**
     * Perform a remote connection validations.  Typically by sending a database message or some other mechanism to validate that the database connection and session are active and can be used for
     * database queries.  Any query submitted by the driver to validate the connection is executed in the context of the current transaction.
     */
    REMOTE

}
