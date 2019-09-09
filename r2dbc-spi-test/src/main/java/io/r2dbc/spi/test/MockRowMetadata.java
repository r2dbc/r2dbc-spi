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

package io.r2dbc.spi.test;

import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.RowMetadata;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public final class MockRowMetadata implements RowMetadata {

    static final Collator COLLATOR;

    static {
        Collator collator = Collator.getInstance(Locale.US);
        collator.setStrength(Collator.SECONDARY);
        COLLATOR = collator;
    }

    private final List<ColumnMetadata> columnMetadatas;

    private MockRowMetadata(List<ColumnMetadata> columnMetadatas) {
        this.columnMetadatas = Assert.requireNonNull(columnMetadatas, "columnMetadatas must not be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MockRowMetadata empty() {
        return builder().build();
    }


    @Override
    public ColumnMetadata getColumnMetadata(int index) {
        return this.columnMetadatas.get(index);
    }

    @Override
    public ColumnMetadata getColumnMetadata(String name) {
        Assert.requireNonNull(name, "name must not be null");

        for (ColumnMetadata columnMetadata : columnMetadatas) {
            if (columnMetadata.getName().equalsIgnoreCase(name)) {
                return columnMetadata;
            }
        }

        throw new NoSuchElementException(String.format("Column %s not found", name));
    }

    @Override
    public List<ColumnMetadata> getColumnMetadatas() {
        return this.columnMetadatas;
    }

    @Override
    public Collection<String> getColumnNames() {
        Set<String> contains = new TreeSet<>(COLLATOR);
        List<String> columnNames = new ArrayList<String>() {

            @Override
            public boolean contains(Object o) {
                return contains.contains(o);
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return contains.containsAll(c);
            }
        };

        for (ColumnMetadata columnMetadata : this.columnMetadatas) {
            columnNames.add(columnMetadata.getName());
            contains.add(columnMetadata.getName());
        }

        return Collections.unmodifiableCollection(columnNames);
    }

    @Override
    public String toString() {
        return "MockRowMetadata{" +
            "columnMetadatas=" + this.columnMetadatas +
            '}';
    }

    public static final class Builder {

        private final List<ColumnMetadata> columnMetadatas = new ArrayList<>();

        private Builder() {
        }

        public MockRowMetadata build() {
            return new MockRowMetadata(this.columnMetadatas);
        }

        public Builder columnMetadata(ColumnMetadata columnMetadata) {
            Assert.requireNonNull(columnMetadata, "columnMetadata must not be null");

            this.columnMetadatas.add(columnMetadata);
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                "columnMetadatas=" + this.columnMetadatas +
                '}';
        }

    }

}
