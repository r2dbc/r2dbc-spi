/*
 * Copyright 2022 the original author or authors.
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
 * Unit tests for {@link Parameters}.
 */
public class ParametersUnitTests {

    @Test
    void parametersShouldBeEqual() {

        assertThat(Parameters.in("foo")).isEqualTo(Parameters.in("foo"));
        assertThat(Parameters.in(String.class)).isEqualTo(Parameters.in(String.class));

        assertThat(Parameters.inOut("foo")).isEqualTo(Parameters.inOut("foo"));
        assertThat(Parameters.inOut(String.class)).isEqualTo(Parameters.inOut(String.class));

        assertThat(Parameters.out(String.class)).isEqualTo(Parameters.out(String.class));
    }

    @Test
    void parametersShouldBeNotEqual() {

        assertThat(Parameters.in(String.class)).isNotEqualTo(Parameters.out(String.class));
        assertThat(Parameters.out(String.class)).isNotEqualTo(Parameters.in(String.class));

        assertThat(Parameters.inOut(String.class)).isNotEqualTo(Parameters.in(String.class));
        assertThat(Parameters.inOut(String.class)).isNotEqualTo(Parameters.out(String.class));

        assertThat(Parameters.in(String.class)).isNotEqualTo(Parameters.inOut(String.class));
        assertThat(Parameters.out(String.class)).isNotEqualTo(Parameters.inOut(String.class));
    }

}
