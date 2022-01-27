/*
 * Copyright 2020-2021 the original author or authors.
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
 * Utility to create {@link Parameter} objects.
 *
 * @since 0.9
 */
public final class Parameters {

    private Parameters() {
    }

    /**
     * Create a {@code NULL IN} parameter using the given {@link Type}.
     *
     * @param type the type to be sent to the database.
     * @return the in parameter.
     * @throws IllegalArgumentException if {@code type} is {@code null}.
     */
    public static Parameter in(Type type) {
        Assert.requireNonNull(type, "Type must not be null");
        return in(type, null);
    }

    /**
     * Create a {@code NULL IN} parameter using type inference and the given {@link Class type} hint.  The actual {@link Type} is inferred during statement execution.
     *
     * @param type the type to be used for type inference.
     * @return the in parameter.
     * @throws IllegalArgumentException if {@code type} is {@code null}.
     */
    public static Parameter in(Class<?> type) {
        Assert.requireNonNull(type, "Type must not be null");
        return in(new DefaultInferredType(type), null);
    }

    /**
     * Create a {@code IN} parameter using the given {@code value}.  The actual {@link Type} is inferred during statement execution.
     *
     * @param value the value to be used for type inference.
     * @return the in parameter.
     * @throws IllegalArgumentException if {@code value} is {@code null}.
     */
    public static Parameter in(Object value) {
        Assert.requireNonNull(value, "Value must not be null");
        return in(new DefaultInferredType(value.getClass()), value);
    }

    /**
     * Create a {@code IN} parameter using the given {@link Type} and {@code value}.
     *
     * @param type  the type to be sent to the database.
     * @param value the value associated with the parameter, can be {@code null}.
     * @return the in parameter.
     * @throws IllegalArgumentException if {@code type} is {@code null}.
     */
    public static Parameter in(Type type, @Nullable Object value) {
        Assert.requireNonNull(type, "Type must not be null");
        return new InParameter(type, value);
    }

    /**
     * Create a {@code OUT} parameter using the given {@link Type}.
     *
     * @param type the type to be sent to the database.
     * @return the out parameter.
     * @throws IllegalArgumentException if {@code type} is {@code null}.
     */
    public static Parameter out(Type type) {
        Assert.requireNonNull(type, "Type must not be null");
        return new OutParameter(type);
    }

    /**
     * Create a {@code OUT} parameter using type inference and the given {@link Class type} hint.  The actual {@link Type} is inferred during statement execution.
     *
     * @param type the type to be used for type inference.
     * @return the out parameter.
     * @throws IllegalArgumentException if {@code type} is {@code null}.
     */
    public static Parameter out(Class<?> type) {
        Assert.requireNonNull(type, "Type must not be null");
        return out(new DefaultInferredType(type));
    }

    /**
     * Create a {@code NULL IN/OUT} parameter using the given {@link Type}.
     *
     * @param type the type to be sent to the database.
     * @return the in/out parameter.
     * @throws IllegalArgumentException if {@code type} is {@code null}.
     */
    public static Parameter inOut(Type type) {
        Assert.requireNonNull(type, "Type must not be null");
        return inOut(type, null);
    }

    /**
     * Create a {@code NULL IN/OUT} parameter using type inference and the given {@link Class type} hint.  The actual {@link Type} is inferred during statement execution.
     *
     * @param type the type to be used for type inference.
     * @return the in/out parameter.
     * @throws IllegalArgumentException if {@code type} is {@code null}.
     */
    public static Parameter inOut(Class<?> type) {
        Assert.requireNonNull(type, "Type must not be null");
        return inOut(new DefaultInferredType(type), null);
    }

    /**
     * Create a {@code IN/OUT} parameter using the given {@code value}.  The actual {@link Type} is inferred during statement execution.^
     *
     * @param value the value to be used for type inference.
     * @return the in/out parameter.
     * @throws IllegalArgumentException if {@code value} is {@code null}.
     */
    public static Parameter inOut(Object value) {
        Assert.requireNonNull(value, "Value must not be null");
        return inOut(new DefaultInferredType(value.getClass()), value);
    }

    /**
     * Create a {@code IN/OUT} parameter using the given {@link Type} and {@code value}.
     *
     * @param type  the type to be sent to the database.
     * @param value the value associated with the parameter, can be {@code null}.
     * @return the in/out parameter.
     * @throws IllegalArgumentException if {@code type} is {@code null}.
     */
    public static Parameter inOut(Type type, @Nullable Object value) {
        Assert.requireNonNull(type, "Type must not be null");
        return new InOutParameter(type, value);
    }

    private static class DefaultParameter implements Parameter {

        private final Type type;

        @Nullable
        private final Object value;

        public DefaultParameter(Type type, @Nullable Object value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public Type getType() {
            return this.type;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Parameter)) {
                return false;
            }

            Parameter that = (Parameter) o;

            if (!getType().equals(that.getType())) {
                return false;
            }
            return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
        }

        @Override
        public int hashCode() {
            int result = getType().hashCode();
            result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
            return result;
        }

    }

    private static class InParameter extends DefaultParameter implements Parameter.In {

        public InParameter(Type type, @Nullable Object value) {
            super(type, value);
        }

        @Override
        public String toString() {
            return "In{" +
                getType() +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof In && !(o instanceof Out) && super.equals(o);
        }

    }

    private static class OutParameter extends DefaultParameter implements Parameter.Out {

        public OutParameter(Type type) {
            super(type, null);
        }

        @Override
        public String toString() {
            return "Out{" +
                getType() +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Out && !(o instanceof In) && super.equals(o);
        }

    }

    private static class InOutParameter extends DefaultParameter implements Parameter.In, Parameter.Out {

        public InOutParameter(Type type, @Nullable Object value) {
            super(type, value);
        }

        @Override
        public String toString() {
            return "InOut{" +
                getType() +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof In && o instanceof Out && super.equals(o);
        }

    }

    private static class DefaultInferredType implements Type.InferredType, Type {

        private final Class<?> javaType;

        DefaultInferredType(Class<?> javaType) {
            this.javaType = javaType;
        }

        @Override
        public Class<?> getJavaType() {
            return this.javaType;
        }

        @Override
        public String getName() {
            return "(inferred)";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Type.InferredType)) {
                return false;
            }

            Type.InferredType that = (Type.InferredType) o;

            return getJavaType().equals(that.getJavaType());
        }

        @Override
        public int hashCode() {
            return getJavaType().hashCode();
        }

        @Override
        public String toString() {
            return "Inferred: " + getJavaType().getName();
        }

    }
}
