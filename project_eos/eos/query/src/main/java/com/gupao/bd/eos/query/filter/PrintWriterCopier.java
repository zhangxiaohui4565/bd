package com.gupao.bd.eos.query.filter;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * 拷贝字符串流字符串
 */
public class PrintWriterCopier {
    private CharArrayWriter charArrayWriter;
    private PrintWriter writer;

    public PrintWriterCopier() {
        this.charArrayWriter = new CharArrayWriter();
        this.writer = new PrintWriter(charArrayWriter);
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public char[] getChars() {
        return charArrayWriter.toCharArray();
    }

    public byte[] getBytes() {
        try {
            return new String(charArrayWriter.toCharArray()).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new String(charArrayWriter.toCharArray()).getBytes();
        }
    }
}
