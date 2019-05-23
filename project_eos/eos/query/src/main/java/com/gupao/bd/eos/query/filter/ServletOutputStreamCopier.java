package com.gupao.bd.eos.query.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 拷贝Servlet输出
 */
public class ServletOutputStreamCopier extends ServletOutputStream {
    private static final int INIT_BUFFER_SIZE = 1024;

    private ByteArrayOutputStream copy;

    public ServletOutputStreamCopier() {
        this.copy = new ByteArrayOutputStream(INIT_BUFFER_SIZE);
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
    }

    @Override
    public void write(int b) throws IOException {
        copy.write(b);
    }

    public byte[] getBytes() {
        return copy.toByteArray();
    }

    @Override
    public void flush() throws IOException {
        copy.flush();
    }
}
