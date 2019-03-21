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
 * Represents a configuration option constant.
 *
 * @param <T> The value type of the option
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
            "name='" + name + '\'' +
            ", sensitive=" + sensitive +
            '}';
    }

    boolean sensitive() {
        return this.sensitive;
    }
}
