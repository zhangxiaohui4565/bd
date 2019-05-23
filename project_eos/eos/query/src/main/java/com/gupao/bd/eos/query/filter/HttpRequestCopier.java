package com.gupao.bd.eos.query.filter;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpMethod;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求复制类
 */
public class HttpRequestCopier extends HttpServletRequestWrapper {
    protected static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    protected static final String FORM_CHARSET = "UTF-8";

    private final byte[] requestBody;

    public HttpRequestCopier(HttpServletRequest request) throws IOException {
        super(request);
        InputStream bodyIns;

        if (isFormPost(request)) {
            bodyIns = getBodyFromServletRequestParameters(request);
        } else {
            bodyIns = request.getInputStream();
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(bodyIns, bos);
        requestBody = bos.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedServletInputStream();
    }

    /**
     * 是否是POST Form请求
     * @param request
     * @return
     */
    private static boolean isFormPost(HttpServletRequest request) {
        String contentType = request.getContentType();
        return (contentType != null && contentType.contains(FORM_CONTENT_TYPE) &&
                HttpMethod.POST.matches(request.getMethod()));
    }

    /**
     * 重构form表单请求body
     */
    private static InputStream getBodyFromServletRequestParameters(HttpServletRequest request) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        Writer writer = new OutputStreamWriter(bos, FORM_CHARSET);

        Map<String, String[]> form = request.getParameterMap();
        for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext();) {
            String name = nameIterator.next();
            List<String> values = Arrays.asList(form.get(name));
            for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext();) {
                String value = valueIterator.next();
                writer.write(URLEncoder.encode(name, FORM_CHARSET));
                if (value != null) {
                    writer.write('=');
                    writer.write(URLEncoder.encode(value, FORM_CHARSET));
                    if (valueIterator.hasNext()) {
                        writer.write('&');
                    }
                }
            }
            if (nameIterator.hasNext()) {
                writer.append('&');
            }
        }
        writer.flush();

        return new ByteArrayInputStream(bos.toByteArray());
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    /**
     * 拷贝输入流
     */
    private class CachedServletInputStream extends ServletInputStream {
        private InputStream baseInputStream;

        CachedServletInputStream() throws IOException {
            baseInputStream = new ByteArrayInputStream(requestBody);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }

        @Override
        public int read() throws IOException {
            return baseInputStream.read();
        }
    }
}
