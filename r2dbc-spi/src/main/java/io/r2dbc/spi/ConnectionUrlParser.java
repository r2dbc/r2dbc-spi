/*
 * Copyright 2019-2021 the original author or authors.
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

import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parser for R2DBC Connection URL's.
 */
abstract class ConnectionUrlParser {

    private static final Set<String> PROHIBITED_QUERY_OPTIONS = Stream.of(ConnectionFactoryOptions.DATABASE,
        ConnectionFactoryOptions.DRIVER, ConnectionFactoryOptions.HOST, ConnectionFactoryOptions.PASSWORD,
        ConnectionFactoryOptions.PORT, ConnectionFactoryOptions.PROTOCOL, ConnectionFactoryOptions.USER).map(Option::name)
        .collect(Collectors.toSet());

    /**
     * Scheme for R2DBC.
     */
    private static final String R2DBC_SCHEME = "r2dbc";

    /**
     * Scheme for R2DBC with SSL enabled.
     */
    private static final String R2DBC_SSL_SCHEME = "r2dbcs";

    static void validate(String url) {

        Assert.requireNonNull(url, "URL must not be null");

        if (!url.startsWith(R2DBC_SCHEME + ":") && !url.startsWith(R2DBC_SSL_SCHEME + ":")) {
            throw new IllegalArgumentException(String.format("URL %s does not start with the %s scheme", url, R2DBC_SCHEME));
        }

        int schemeSpecificPartIndex = url.indexOf("://");
        int driverPartIndex;

        if (url.startsWith(R2DBC_SSL_SCHEME)) {
            driverPartIndex = R2DBC_SSL_SCHEME.length() + 1;
        } else {
            driverPartIndex = R2DBC_SCHEME.length() + 1;
        }

        if (schemeSpecificPartIndex == -1 || driverPartIndex >= schemeSpecificPartIndex) {
            throw new IllegalArgumentException(String.format("Invalid URL: %s", url));
        }

        String[] schemeParts = url.split(":", 3);

        String driver = schemeParts[1];
        if (driver.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format("Empty driver in URL: %s", url));
        }
    }

    static ConnectionFactoryOptions parseQuery(CharSequence url) {

        String urlToUse = url.toString();
        validate(urlToUse);

        // R2DBC URL must contain at least two colons in the scheme part (r2dbc:<some driver>:).
        String[] schemeParts = urlToUse.split(":", 3);

        String scheme = schemeParts[0];
        String driver = schemeParts[1];
        String protocol = schemeParts[2];

        int schemeSpecificPartIndex = urlToUse.indexOf("://");
        String rewrittenUrl = scheme + urlToUse.substring(schemeSpecificPartIndex);

        URI uri = URI.create(rewrittenUrl);

        ConnectionFactoryOptions.Builder builder = ConnectionFactoryOptions.builder();

        if (scheme.equals(R2DBC_SSL_SCHEME)) {
            builder.option(ConnectionFactoryOptions.SSL, true);
        }

        builder.option(ConnectionFactoryOptions.DRIVER, driver);

        int protocolEnd = protocol.indexOf("://");
        if (protocolEnd != -1) {
            protocol = protocol.substring(0, protocolEnd);

            if (!protocol.trim().isEmpty()) {
                builder.option(ConnectionFactoryOptions.PROTOCOL, protocol);
            }
        }

        if (hasText(uri.getHost())) {
            builder.option(ConnectionFactoryOptions.HOST, decode(uri.getHost().trim()).toString());

            if (hasText(uri.getRawUserInfo())) {
                parseUserinfo(uri.getRawUserInfo(), builder);
            }
        } else if (hasText(uri.getRawAuthority())) {

            String authorityToUse = uri.getRawAuthority();

            if (authorityToUse.contains("@")) {

                // to avoid problems when authority strings contains special characters like '@'
                int atIndex = authorityToUse.lastIndexOf('@');
                String userinfo = authorityToUse.substring(0, atIndex);
                authorityToUse = authorityToUse.substring(atIndex + 1);

                if (!userinfo.isEmpty()) {
                    parseUserinfo(userinfo, builder);
                }
            }

            builder.option(ConnectionFactoryOptions.HOST, decode(authorityToUse.trim()).toString());
        }

        if (uri.getPort() != -1) {
            builder.option(ConnectionFactoryOptions.PORT, uri.getPort());
        }

        if (hasText(uri.getPath())) {
            String path = uri.getPath().substring(1).trim();
            if (hasText(path)) {
                builder.option(ConnectionFactoryOptions.DATABASE, path);
            }
        }

        if (hasText(uri.getRawQuery())) {
            parseQuery(uri.getRawQuery().trim(), (k, v) -> {

                if (PROHIBITED_QUERY_OPTIONS.contains(k)) {
                    throw new IllegalArgumentException(
                        String.format("URL %s must not declare option %s in the query string", url, k));
                }

                builder.option(Option.valueOf(k), v);
            });
        }

        return builder.build();
    }

    /**
     * Parse a {@link CharSequence query string} and decode percent encoding according to RFC3986, section 2.4.
     * Percent-encoded octets are decoded using UTF-8.
     *
     * @param s             input text
     * @param tupleConsumer consumer notified on tuple creation
     * @link https://tools.ietf.org/html/rfc3986#section-2.4
     * @see StandardCharsets#UTF_8
     */
    static void parseQuery(CharSequence s, BiConsumer<String, String> tupleConsumer) {

        QueryStringParser parser = QueryStringParser.create(s);

        while (!parser.isFinished()) {

            CharSequence name = parser.parseName();
            CharSequence value = parser.isFinished() ? null : parser.parseValue();

            if (name.length() != 0 && value != null) {
                tupleConsumer.accept(decode(name).toString(), decode(value).toString());
            }
        }
    }

    private static void parseUserinfo(String s, ConnectionFactoryOptions.Builder builder) {

        if (!s.contains(":")) {
            String user = decode(s).toString();
            builder.option(ConnectionFactoryOptions.USER, user);
            return;
        }

        String[] userinfo = s.split(":", 2);

        String user = decode(userinfo[0]).toString();
        if (!user.isEmpty()) {
            builder.option(ConnectionFactoryOptions.USER, user);
        }

        CharSequence password = decode(userinfo[1]);
        if (password.length() != 0) {
            builder.option(ConnectionFactoryOptions.PASSWORD, password);
        }
    }

    /**
     * Simplified fork of {@link URLDecoder}.  The supplied encoding is used to determine what characters are represented
     * by any consecutive sequences of the form {@code %xy}.
     *
     * @param s the {@link CharSequence} to decode.
     * @return the newly decoded {@code CharSequence}.
     * @see URLDecoder
     */
    private static CharSequence decode(CharSequence s) {

        boolean encoded = false;
        int numChars = s.length();
        StringBuffer sb = new StringBuffer(numChars > 500 ? numChars / 2 : numChars);
        int i = 0;

        char c;
        byte[] bytes = null;

        while (i < numChars) {
            c = s.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    i++;
                    encoded = true;
                    break;
                case '%':
                    /*
                     * Starting with this instance of %, process all
                     * consecutive substrings of the form %xy. Each
                     * substring %xy will yield a byte. Convert all
                     * consecutive  bytes obtained this way to whatever
                     * character(s) they represent in the provided
                     * encoding.
                     */
                    try {

                        // (numChars-i)/3 is an upper bound for the number
                        // of remaining bytes
                        if (bytes == null) {
                            bytes = new byte[(numChars - i) / 3];
                        }
                        int pos = 0;

                        while (((i + 2) < numChars) && (c == '%')) {
                            int v = Integer.parseInt(s.subSequence(i + 1, i + 3).toString(), 16);
                            if (v < 0) {
                                throw new IllegalArgumentException(
                                    "URLDecoder: Illegal hex characters in escape (%) pattern - negative value");
                            }
                            bytes[pos++] = (byte) v;
                            i += 3;
                            if (i < numChars) {
                                c = s.charAt(i);
                            }
                        }

                        // A trailing, incomplete byte encoding such as
                        // "%x" will cause an exception to be thrown

                        if ((i < numChars) && (c == '%')) {
                            throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
                        }

                        sb.append(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes, 0, pos)));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                            "URLDecoder: Illegal hex characters in escape (%) pattern - " + e.getMessage());
                    }
                    encoded = true;
                    break;
                default:
                    sb.append(c);
                    i++;
                    break;
            }
        }

        return (encoded ? sb : s);
    }

    private static boolean hasText(@Nullable String s) {
        return s != null && !s.isEmpty();
    }

    private ConnectionUrlParser() {
    }

    /**
     * Parser for RFC3986-style query strings such as {@code key1=value1&key2=value2}.
     *
     * @link https://tools.ietf.org/html/rfc3986#section-3.4
     */
    static class QueryStringParser {

        /**
         * carriage return (ASCII 13).
         */
        static final char CR = '\r';

        /**
         * line feed (ASCII 10).
         */
        static final char LF = '\n';

        /**
         * space (ASCII 32).
         */
        static final char SPACE = ' ';

        /**
         * horizontal-tab (ASCII 9).
         */
        static final char TAB = '\t';

        private final CharSequence input;

        private final Cursor state;

        private final BitSet delimiters = new BitSet(256);

        private QueryStringParser(CharSequence input) {
            this.input = input;
            this.state = new Cursor(input.length());
            this.delimiters.set('&'); // ampersand, tuple separator
        }

        /**
         * Creates a new {@link QueryStringParser} given the {@code input}.
         *
         * @param input must not be {@code null}
         * @return a new {@link QueryStringParser} instance
         */
        static QueryStringParser create(CharSequence input) {
            return new QueryStringParser(input);
        }

        /**
         * Returns whether parsing is finished.
         *
         * @return {@literal true} if parsing is finished; {@literal false} otherwise
         */
        boolean isFinished() {
            return state.isFinished();
        }

        /**
         * Extracts a sequence of characters identifying the name of the key-value tuple.
         *
         * @return name of the key-value pair
         * @throws IllegalStateException if parsing is already {@link #isFinished() finished}
         */
        CharSequence parseName() {

            if (this.state.isFinished()) {
                throw new IllegalStateException("Parsing is finished");
            }

            delimiters.set('=');
            return parseToken();
        }

        /**
         * Extracts a sequence of characters identifying the name of the key-value tuple.
         *
         * @return value of the key-value pair, can be {@code null}
         * @throws IllegalStateException if parsing is already {@link #isFinished() finished}
         */
        @Nullable
        CharSequence parseValue() {
            if (this.state.isFinished()) {
                throw new IllegalStateException("Parsing is finished");
            }

            int delim = this.input.charAt(this.state.getParsePosition());
            this.state.incrementParsePosition();

            if (delim == '=') {
                delimiters.clear('=');
                try {
                    return parseToken();
                } finally {
                    if (!isFinished()) {
                        this.state.incrementParsePosition();
                    }
                }
            }

            return null;
        }

        /**
         * Extracts from the sequence of chars a token terminated with any of the given delimiters discarding semantically
         * insignificant whitespace characters.
         */
        private CharSequence parseToken() {

            StringBuilder dst = new StringBuilder();

            boolean whitespace = false;

            while (!this.state.isFinished()) {
                char current = this.input.charAt(this.state.getParsePosition());
                if (delimiters.get(current)) {
                    break;
                } else if (isWhitespace(current)) {
                    skipWhiteSpace();
                    whitespace = true;
                } else {
                    if (whitespace && dst.length() > 0) {
                        dst.append(' ');
                    }
                    copyContent(dst);
                    whitespace = false;
                }
            }

            return dst;
        }

        /**
         * Skips semantically insignificant whitespace characters and moves the cursor to the closest non-whitespace
         * character.
         */
        private void skipWhiteSpace() {
            int pos = this.state.getParsePosition();

            for (int i = this.state.getParsePosition(); i < this.state.getUpperBound(); i++) {
                char current = this.input.charAt(i);
                if (!isWhitespace(current)) {
                    break;
                }
                pos++;
            }

            this.state.updatePos(pos);
        }

        /**
         * Transfers content into the destination buffer until a whitespace character or any of the given delimiters is
         * encountered.
         *
         * @param target destination buffer
         */
        private void copyContent(StringBuilder target) {
            int pos = this.state.getParsePosition();

            for (int i = this.state.getParsePosition(); i < this.state.getUpperBound(); i++) {
                char current = this.input.charAt(i);
                if (delimiters.get(current) || isWhitespace(current)) {
                    break;
                }
                pos++;
                target.append(current);
            }

            this.state.updatePos(pos);
        }

        private static boolean isWhitespace(char ch) {
            return ch == SPACE || ch == TAB || ch == CR || ch == LF;
        }

    }

    /**
     * Parse cursor state.
     */
    private static class Cursor {

        private final int upperBound;

        private int pos;

        Cursor(int upperBound) {
            this.upperBound = upperBound;
            this.pos = 0;
        }

        void incrementParsePosition() {
            updatePos(getParsePosition() + 1);
        }

        int getUpperBound() {
            return this.upperBound;
        }

        int getParsePosition() {
            return this.pos;
        }

        void updatePos(final int pos) {
            this.pos = pos;
        }

        boolean isFinished() {
            return this.pos >= this.upperBound;
        }

    }

}
