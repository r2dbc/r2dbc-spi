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

import org.reactivestreams.Publisher;

/**
 * A common interface defining methods for allocation/release lifecycle control. The typical use case for this is to notify resources.
 *
 * <p>Can be implemented by connections.  Components allocating/releasing connections notify resources implementing this interface about the ongoing activity so that resources can react in a proper
 * way (e.g. allocating or releasing associated resources).
 *
 * @see Connection
 * @since 0.9
 */
public interface Lifecycle {

	/**
	 * Notifies the resource about its allocation.
	 * The method is called after creating a new resource or allocating a cached resource before applying any requests.  The resource is ready for usage after completion of the returned
	 * {@link Publisher}.
	 *
	 * @return a {@link Publisher} that indicates that the resource is ready for usage
	 */
	Publisher<Void> postAllocate();

	/**
	 * Notifies the resource that it is about to be released.
	 * The method is called after finishing its usage and prior to its release/close.  The resource is ready for being released after completion of the returned {@link Publisher}.
	 *
	 * @return a {@link Publisher} that indicates that the resource is ready to be released
	 */
	Publisher<Void> preRelease();

}
