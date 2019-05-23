package com.gupao.bd.eos.query.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 拷贝HttpServletResponse
 */
public class HttpServletResponseCopier extends HttpServletResponseWrapper {
    private int statusCode;
    private ServletOutputStreamCopier streamCopier;
    private PrintWriterCopier         writerCopier;
    private boolean                   useWriter;
    private List<Header> headers;

    public HttpServletResponseCopier(HttpServletResponse response) throws IOException {
        super(response);
        useWriter = true;
        headers = new ArrayList<>();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writerCopier != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (streamCopier == null) {
            useWriter = false;
            streamCopier = new ServletOutputStreamCopier();
        }

        return streamCopier;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (streamCopier != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writerCopier == null) {
            useWriter = true;
            writerCopier = new PrintWriterCopier();
        }

        return writerCopier.getWriter();
    }

    public byte[] getBytes() {
        if (streamCopier == null && writerCopier == null) {
            return new byte[0];
        }

        return useWriter ? writerCopier.getBytes() : streamCopier.getBytes();
    }

    public byte[] getStreamBytes() {
        if (useWriter) {
            throw new IllegalStateException("already use writer, please call getWriterChars()");
        }

        return (streamCopier == null) ? new byte[0] : streamCopier.getBytes();
    }

    public char[] getWriterChars() {
        if (!useWriter) {
            throw new IllegalStateException("already use outputStream, please call getStreamBytes()");
        }

        return (writerCopier == null) ? new char[0] : writerCopier.getChars();
    }

    /**
     * The default behavior of this method is to call sendError(int sc, String msg)
     * on the wrapped response object.
     */
    @Override
    public void sendError(int sc, String msg) throws IOException {
        ((HttpServletResponse)getResponse()).sendError(sc, msg);
        this.statusCode = sc;
    }

    /**
     * The default behavior of this method is to call sendError(int sc)
     * on the wrapped response object.
     */
    @Override
    public void sendError(int sc) throws IOException {
        ((HttpServletResponse)getResponse()).sendError(sc);
        this.statusCode = sc;
    }

    @Override
    public void setStatus(int sc) {
        super.setStatus(sc);
        this.statusCode = sc;
    }

    /**
     * The default behavior of this method is to call setStatus(int sc, String sm)
     * on the wrapped response object.
     */
    @Override
    public void setStatus(int sc, String sm) {
        super.setStatus(sc, sm);
        this.statusCode = sc;
    }

    @Override
    public void setHeader(String name, String value) {
        super.setHeader(name, value);
        this.headers.add(new Header(name, value));
    }

    @Override
    public void addHeader(String name, String value) {
        super.addHeader(name, value);
        this.headers.add(new Header(name, value));
    }

    @Override
    public void setIntHeader(String name, int value) {
        super.setIntHeader(name, value);
        this.headers.add(new Header(name, String.valueOf(value)));
    }

    @Override
    public void addIntHeader(String name, int value) {
        super.addIntHeader(name, value);
        this.headers.add(new Header(name, String.valueOf(value)));
    }

    @Override
    public void setDateHeader(String name, long date) {
        super.setDateHeader(name, date);
        this.headers.add(new Header(name, String.valueOf(date)));
    }

    @Override
    public void addDateHeader(String name, long date) {
        super.addDateHeader(name, date);
        this.headers.add(new Header(name, String.valueOf(date)));
    }

    public List<Header> getHeaders() {
        return Collections.unmodifiableList(this.headers);
    }

    @Override
    public int getStatus() {
        return statusCode;
    }

    public boolean isUseWriter() {
        return useWriter;
    }
}