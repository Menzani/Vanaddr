package com.vanaddr;

import java.io.Writer;

class CharArrayWriter extends Writer {
    private final char[] buf;
    private int count;

    CharArrayWriter(char[] buf) {
        this.buf = buf;
    }

    @Override
    public void write(int c) {
        buf[count++] = (char) c;
    }

    @Override
    public void write(char[] c, int off, int len) {
    }

    @Override
    public void write(String str, int off, int len) {
    }

    @Override
    public CharArrayWriter append(CharSequence csq) {
        return this;
    }

    @Override
    public CharArrayWriter append(CharSequence csq, int start, int end) {
        return this;
    }

    @Override
    public CharArrayWriter append(char c) {
        return this;
    }

    public void reset() {
        count = 0;
    }

    @Override
    public String toString() {
        return new String(buf, 0, count);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}
