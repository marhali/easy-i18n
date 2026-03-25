package de.marhali.easyi18n.infra.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.*;

/**
 * Extended version of {@link Properties} with an underlying {@link LinkedHashMap} to keep insertion order.
 *
 * @author marhali
 */
public class LinkedProperties extends Properties {

    private final transient @NotNull Map<Object, Object> properties;

    public LinkedProperties() {
        this.properties = new LinkedHashMap<>();
    }

    public LinkedProperties(int initialCapacity) {
        this.properties = new LinkedHashMap<>(initialCapacity);
    }

    public @NotNull Map<Object, Object> getProperties() {
        return this.properties;
    }

    @Override
    public Object get(Object key) {
        return this.properties.get(key);
    }

    @Override
    public @NotNull Set<Object> keySet() {
        return this.properties.keySet();
    }

    @Override
    public @NotNull Set<Map.Entry<Object, Object>> entrySet() {
        return this.properties.entrySet();
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        return this.properties.put(key, value);
    }

    @Override
    public void store(Writer writer, String comments) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void store(OutputStream out, @Nullable String comments) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public synchronized String toString() {
        return this.properties.toString();
    }

    public void store(Writer writer) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(writer) {
            @Override
            public void newLine() throws IOException {
                // IntelliJ handles break lines only with \n
                write("\n");
            }
        }) {
            boolean escapeUnicode = false;

            synchronized (this) {
                for (Map.Entry<Object, Object> entry : entrySet()) {
                    String key = String.valueOf(entry.getKey());
                    String value = String.valueOf(entry.getValue());

                    String saveKey = saveConvert(key, true, escapeUnicode);
                    // No need to escape embedded and trailing spaces for value, hence pass false to flag
                    String saveValue = saveConvert(value, false, escapeUnicode);

                    bufferedWriter.write(key + "=" + value);
                    bufferedWriter.newLine();
                }
            }

            bufferedWriter.flush();
        }
    }

    /*
     * PRIVATE METHOD COPIED FROM SUPERCLASS (Properties.java)
     *
     * Converts unicodes to encoded &#92;uxxxx and escapes
     * special characters with a preceding slash
     */
    private String saveConvert(String theString,
                               boolean escapeSpace,
                               boolean escapeUnicode) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuilder outBuffer = new StringBuilder(bufLen);
        HexFormat hex = HexFormat.of().withUpperCase();
        for(int x=0; x<len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\'); outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch(aChar) {
                case ' ':
                    if (x == 0 || escapeSpace)
                        outBuffer.append('\\');
                    outBuffer.append(' ');
                    break;
                case '\t':outBuffer.append('\\'); outBuffer.append('t');
                    break;
                case '\n':outBuffer.append('\\'); outBuffer.append('n');
                    break;
                case '\r':outBuffer.append('\\'); outBuffer.append('r');
                    break;
                case '\f':outBuffer.append('\\'); outBuffer.append('f');
                    break;
                case '=': // Fall through
                case ':': // Fall through
                case '#': // Fall through
                case '!':
                    outBuffer.append('\\'); outBuffer.append(aChar);
                    break;
                default:
                    if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode ) {
                        outBuffer.append("\\u");
                        outBuffer.append(hex.toHexDigits(aChar));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }
}
