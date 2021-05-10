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

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;

/**
 * Definition of generic SQL types.
 *
 * @since 0.9
 */
public enum R2dbcType implements Type {

    // ----------------------------------------------------
    // Character types
    // ----------------------------------------------------

    /**
     * Identifies the generic SQL type {@code CHAR}.
     */
    CHAR(String.class),

    /**
     * Identifies the generic SQL type {@code VARCHAR}.
     */
    VARCHAR(String.class),

    /**
     * Identifies the generic SQL type {@code NCHAR}.
     */
    NCHAR(String.class),

    /**
     * Identifies the generic SQL type {@code NVARCHAR}.
     */
    NVARCHAR(String.class),

    /**
     * Identifies the generic SQL type {@code CLOB}.
     * Note that drivers may default to {@link Clob} if materializing a {@code CLOB} value requires additional database communication.
     */
    CLOB(String.class),

    /**
     * Identifies the generic SQL type {@code NCLOB}.
     * Note that drivers may default to {@link Clob} if materializing a {@code NCLOB} value requires additional database communication.
     */
    NCLOB(String.class),

    // ----------------------------------------------------
    // Boolean types
    // ----------------------------------------------------

    /**
     * Identifies the generic SQL type {@code BOOLEAN}.
     */
    BOOLEAN(Boolean.class),

    // ----------------------------------------------------
    // Binary types
    // ----------------------------------------------------

    /**
     * Identifies the generic SQL type {@code BINARY}.
     */
    BINARY(ByteBuffer.class),

    /**
     * Identifies the generic SQL type {@code VARBINARY}.
     */
    VARBINARY(ByteBuffer.class),

    /**
     * Identifies the generic SQL type {@code BLOB}.
     * Note that drivers may default to {@link Blob} if materializing a {@code BLOB} value requires additional database communication.
     */
    BLOB(ByteBuffer.class),

    // ----------------------------------------------------
    // Numeric types
    // ----------------------------------------------------

    /**
     * Identifies the generic SQL type {@code INTEGER}.
     */
    INTEGER(Integer.class),

    /**
     * Identifies the generic SQL type {@code TINYINT}.
     */
    TINYINT(Byte.class),

    /**
     * Identifies the generic SQL type {@code SMALLINT}.
     */
    SMALLINT(Short.class),

    /**
     * Identifies the generic SQL type {@code BIGINT}.
     */
    BIGINT(Long.class),

    /**
     * Identifies the generic SQL type {@code NUMERIC}.
     */
    NUMERIC(BigDecimal.class),

    /**
     * Identifies the generic SQL type {@code DECIMAL}.
     */
    DECIMAL(BigDecimal.class),

    /**
     * Identifies the generic SQL type {@code FLOAT}.
     */
    FLOAT(Double.class),

    /**
     * Identifies the generic SQL type {@code REAL}.
     */
    REAL(Float.class),

    /**
     * Identifies the generic SQL type {@code DOUBLE}.
     */
    DOUBLE(Double.class),

    // ----------------------------------------------------
    // Date/Time types
    // ----------------------------------------------------

    /**
     * Identifies the generic SQL type {@code DATE}.
     */
    DATE(LocalDate.class),

    /**
     * Identifies the generic SQL type {@code TIME}.
     */
    TIME(LocalTime.class),

    /**
     * Identifies the generic SQL type {@code TIME WITH TIME ZONE}.
     */
    TIME_WITH_TIME_ZONE(OffsetTime.class),

    /**
     * Identifies the generic SQL type {@code TIMESTAMP}.
     */
    TIMESTAMP(LocalDateTime.class),

    /**
     * Identifies the generic SQL type {@code TIMESTAMP WITH TIME ZONE}.
     */
    TIMESTAMP_WITH_TIME_ZONE(OffsetDateTime.class),

    /**
     * Identifies the generic SQL type {@code ARRAY}.
     */
    COLLECTION(Object[].class);

    private final Class<?> javaType;

    R2dbcType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> getJavaType() {
        return this.javaType;
    }

    @Override
    public String getName() {
        return name();
    }
}
