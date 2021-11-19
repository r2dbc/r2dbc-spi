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

import static aQute.bnd.annotation.Cardinality.MULTIPLE;
import static aQute.bnd.annotation.Resolution.OPTIONAL;

import java.util.ServiceLoader;

import aQute.bnd.annotation.spi.ServiceConsumer;

/**
 * A Java Service interface for implementations to examine a collection of {@link ConnectionFactoryOptions} and optionally return an implementation of {@link ConnectionFactory}.
 * <p>{@link ConnectionFactoryProvider} implementations are typically discovered by {@link ConnectionFactories} from the class path using {@link ServiceLoader}.
 *
 * @see ConnectionFactoryOptions
 * @see ConnectionFactory
 * @see ConnectionFactories
 * @see ServiceLoader
 */

@ServiceConsumer(
    // This instance enables resolving a provider at _assembly_ without incurring a strict runtime
    // dependency on Service Loader Mediator (SML) impl
    cardinality = MULTIPLE, effective = "active", value = ConnectionFactoryProvider.class)
@ServiceConsumer(
    // This instance enables SML to instrument ServiceLoader calls _if SML is present_
    // (without preventing the bundles from resolving if it is not)
    cardinality = MULTIPLE, resolution = OPTIONAL, value = ConnectionFactoryProvider.class)
public interface ConnectionFactoryProvider {

    /**
     * Creates a new {@link ConnectionFactory} given a collection of {@link ConnectionFactoryOptions}.  This method is only called if a previous invocation of
     * {@link #supports(ConnectionFactoryOptions)} has returned {@code true}.
     *
     * @param connectionFactoryOptions a collection of {@link ConnectionFactoryOptions}
     * @return the {@link ConnectionFactory} created from this collection of {@link ConnectionFactoryOptions}
     * @throws IllegalArgumentException if {@code connectionFactoryOptions} is {@code null}
     */
    ConnectionFactory create(ConnectionFactoryOptions connectionFactoryOptions);

    /**
     * Whether this {@link ConnectionFactoryProvider} supports this collection of {@link ConnectionFactoryOptions}.
     *
     * @param connectionFactoryOptions a collection of {@link ConnectionFactoryOptions}
     * @return {@code true} if this {@link ConnectionFactoryProvider} supports this collection of {@link ConnectionFactoryOptions}, {@code false} otherwise
     * @throws IllegalArgumentException if {@code connectionFactoryOptions} is {@code null}
     */
    boolean supports(ConnectionFactoryOptions connectionFactoryOptions);

    /**
     * Returns the driver identifier used by the driver.
     * The identifier for drivers would be the value applicable to {@link ConnectionFactoryOptions#DRIVER}
     *
     * @return the driver identifier used by the driver
     */
    String getDriver();

}
