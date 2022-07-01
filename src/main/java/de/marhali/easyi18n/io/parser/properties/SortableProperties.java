package de.marhali.easyi18n.io.parser.properties;

import de.marhali.easyi18n.util.IntelliJBufferedWriter;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.*;

/**
 * Extends {@link Properties} class to support sorted or non-sorted keys.
 * @author marhali
 */
public class SortableProperties extends Properties {

    private final transient Map<Object, Object> properties;

    public SortableProperties(boolean sort) {
        this.properties = sort ? new TreeMap<>() : new LinkedHashMap<>();
    }

    public Map<Object, Object> getProperties() {
        return this.properties;
    }

    @Override
    public Object get(Object key) {
        return this.properties.get(key);
    }

    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(this.properties.keySet());
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return this.properties.entrySet();
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        return this.properties.put(key, value);
    }

    @Override
    @Deprecated
    public void store(OutputStream out, @Nullable String comments) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    @Deprecated
    public void store(Writer writer, String comments) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void store(Writer writer) throws IOException {
        try(IntelliJBufferedWriter bw = new IntelliJBufferedWriter(writer)) {
            boolean escUnicode = false;

            synchronized (this) {
                for (Map.Entry<Object, Object> e : entrySet()) {
                    String key = String.valueOf(e.getKey());
                    String val = String.valueOf(e.getValue());
                    key = saveConvert(key, true, escUnicode);
                    /* No need to escape embedded and trailing spaces for value, hence
                     * pass false to flag.
                     */
                    val = saveConvert(val, false, escUnicode);
                    bw.write(key + "=" + val);
                    bw.newLine();
                }
            }
            bw.flush();
        }
    }

    /*
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
                        outBuffer.append(Integer.toHexString(aChar));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    @Override
    public synchronized String toString() {
        return this.properties.toString();
    }
}