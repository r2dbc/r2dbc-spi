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

import java.util.Objects;

/**
 * Represents a configuration option constant.
 *
 * @param <T> The value type of the option when configuring a value programmatically
 * @see ConnectionFactoryOptions
 * @see TransactionDefinition
 */
public final class Option<T> {

    private static final ConstantPool<Option<?>> CONSTANTS = new ConstantPool<Option<?>>() {

        @Override
        Option<?> createConstant(String name, boolean sensitive) {
            return new Option<>(name, sensitive);
        }

    };

    private final String name;

    private final boolean sensitive;

    private Option(String name, boolean sensitive) {
        this.name = name;
        this.sensitive = sensitive;
    }

    /**
     * Returns a constant singleton instance of the sensitive option.
     *
     * @param name the name of the option to return
     * @param <T>  the value type of the option
     * @return a constant singleton instance of the option
     * @throws IllegalArgumentException if {@code name} is {@code null} or empty
     */
    @SuppressWarnings("unchecked")
    public static <T> Option<T> sensitiveValueOf(String name) {
        Assert.requireNonNull(name, "name must not be null");
        Assert.requireNonEmpty(name, "name must not be empty");

        return (Option<T>) CONSTANTS.valueOf(name, true);
    }

    /**
     * Returns a constant singleton instance of the option.
     *
     * @param name the name of the option to return
     * @param <T>  the value type of the option
     * @return a constant singleton instance of the option
     * @throws IllegalArgumentException if {@code name} is {@code null} or empty
     */
    @SuppressWarnings("unchecked")
    public static <T> Option<T> valueOf(String name) {
        Assert.requireNonNull(name, "name must not be null");
        Assert.requireNonEmpty(name, "name must not be empty");

        return (Option<T>) CONSTANTS.valueOf(name, false);
    }

    /**
     * Casts an object to the class or interface represented by this option object.
     *
     * @param obj the object to be cast
     * @return the object after casting, or null if obj is {@code null}.
     * @since 0.9
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public T cast(@Nullable Object obj) {

        if (obj == null) {
            return null;
        }

        return (T) obj;
    }

    /**
     * Returns the name of the option.
     *
     * @return the name of the option
     */
    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return "Option{" +
            "name='" + this.name + '\'' +
            ", sensitive=" + this.sensitive +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Option<?> option = (Option<?>) o;
        return this.sensitive == option.sensitive &&
            this.name.equals(option.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.sensitive);
    }

    boolean sensitive() {
        return this.sensitive;
    }

}
