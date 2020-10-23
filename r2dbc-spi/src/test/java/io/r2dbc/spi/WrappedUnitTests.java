/*
 * Copyright 2020 the original author or authors.
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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Wrapped}.
 */
class WrappedUnitTests {

    @Test
    void unwrap() {
        Person person = new Person() {

        };

        Foo foo = new Foo(person);
        Bar bar = new Bar(foo);
        Baz baz = new Baz(bar);

        assertThat(baz.unwrap(Foo.class)).isSameAs(foo);
        assertThat(baz.unwrap(Bar.class)).isSameAs(bar);
        assertThat(baz.unwrap(Baz.class)).isSameAs(baz);

        assertThat(foo.unwrap(Foo.class)).isSameAs(foo);
        assertThat(foo.unwrap(Bar.class)).isNull();

        // unwrap to the outer most class
        assertThat(foo.unwrap(Person.class)).isSameAs(foo);
        assertThat(bar.unwrap(Person.class)).isSameAs(bar);
        assertThat(baz.unwrap(Person.class)).isSameAs(baz);

        assertThat(foo.unwrap(String.class)).isNull();
        assertThat(bar.unwrap(String.class)).isNull();
        assertThat(baz.unwrap(String.class)).isNull();
    }

    interface Person {

    }

    static abstract class AbstractPerson implements Person, Wrapped<Person> {

        Person person;

        public AbstractPerson(Person person) {
            this.person = person;
        }

        @Override
        public Person unwrap() {
            return this.person;
        }

    }

    static class Foo extends AbstractPerson implements Person {

        public Foo(Person person) {
            super(person);
        }
    }

    static class Bar extends AbstractPerson {

        public Bar(Person person) {
            super(person);
        }
    }

    static class Baz extends AbstractPerson {

        public Baz(Person person) {
            super(person);
        }
    }

}
