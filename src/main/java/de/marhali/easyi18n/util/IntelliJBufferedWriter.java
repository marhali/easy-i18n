package de.marhali.easyi18n.util;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * IntelliJ aware BufferedWriter implementation.
 * (Document PSI uses \n as line separator)
 * @author marhali
 */
public class IntelliJBufferedWriter extends BufferedWriter {
    public IntelliJBufferedWriter(@NotNull Writer out) {
        super(out);
    }

    public IntelliJBufferedWriter(@NotNull Writer out, int sz) {
        super(out, sz);
    }

    @Override
    public void newLine() throws IOException {
        write("\n");
    }
}
