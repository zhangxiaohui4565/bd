package com.gupao.bd.eos.query.filter;

import org.elasticsearch.common.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * 打印请求输入/输出
 *
 */
public class RequestDumpFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RequestDumpFilter.class);

    private static final String REQ_START_TIME_KEY = "__req_start_time__";

    private final static String DUMP_ALL_PARAM = "__dump_all_requests__";
    private final static String EXCLUDED_URI_ROUTE_NAME = "excludedUriRoutes";
    private boolean dumpAll = false;
    private List<String> excludedUriRoutes;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String uriRoutes = filterConfig.getInitParameter(EXCLUDED_URI_ROUTE_NAME);
        if (null == uriRoutes) {
            excludedUriRoutes = Collections.emptyList();
        } else {
            excludedUriRoutes = Arrays.asList(uriRoutes.split(","));
        }

        String dumpAll = filterConfig.getInitParameter(DUMP_ALL_PARAM);
        if (Strings.hasText(dumpAll)) {
            this.dumpAll = Boolean.parseBoolean(dumpAll);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hRequest = null;
        HttpServletResponse hResponse = null;

        boolean isHttpRequest = false;
        if (request instanceof HttpServletRequest) {
            hRequest = (HttpServletRequest) request;
        }
        if (response instanceof HttpServletResponse) {
            hResponse = (HttpServletResponse) response;
            isHttpRequest = true;
        }

        if (isHttpRequest) {
            request.setAttribute(REQ_START_TIME_KEY, System.currentTimeMillis());
            HttpServletResponseCopier responseCopier = new HttpServletResponseCopier(hResponse);
            HttpRequestCopier requestCopier = new HttpRequestCopier(hRequest);
            long duration = 0;
            try {
                long start = System.currentTimeMillis();
                chain.doFilter(requestCopier, responseCopier);
                duration = System.currentTimeMillis() - start;
            } finally {
                try {
                    writeResponse(response, responseCopier);
                    Level dumpLevel = shouldDump(requestCopier, responseCopier);
                    if (null != dumpLevel) {
                        dump(requestCopier, responseCopier, dumpLevel, duration);
                    }
                } catch (Exception e) {
                    logger.error("RequestDumpFilter doFilter error", e);
                }
            }
        }  else { // 非HTTP请求
            chain.doFilter(request, response);
        }
    }

    private void dump(HttpRequestCopier requestCopier, HttpServletResponseCopier responseCopier, Level dumpLevel, long duration) throws IOException {
        LinesLogger linesLogger = new LinesLogger(dumpLevel);
        linesLogger.start();
        linesLogger.doLog("General:");
        linesLogger.doLog("duration", duration + "ms");
        linesLogger.doLog("time", formatTime((Long)requestCopier.getAttribute(REQ_START_TIME_KEY)));
        linesLogger.doLog("requestURL", requestCopier.getMethod() + " " + requestCopier.getRequestURL().toString());
        linesLogger.doLog("queryString", requestCopier.getQueryString());
        linesLogger.doLog("remoteAddress", requestCopier.getRemoteAddr());
        linesLogger.doLog("servletPath", requestCopier.getServletPath());
        linesLogger.doLog("isSecure", Boolean.valueOf(requestCopier.isSecure()).toString());
        linesLogger.doLog("authType", requestCopier.getAuthType());

        linesLogger.doLog("Headers:");
        Enumeration headerNames = requestCopier.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            if ("cookie".equals(headerName)) {
                continue;
            }
            Enumeration headerValues = requestCopier.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                linesLogger.doLog(headerName, String.valueOf(headerValues.nextElement()));
            }
        }

        linesLogger.doLog("Cookies:");
        Cookie cookies[] = requestCopier.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                linesLogger.doLog(cookies[i].getName(), cookies[i].getValue());
            }
        }
        linesLogger.doLog("Body:");
        linesLogger.doLog(new String(requestCopier.getRequestBody()));

        linesLogger.doLog("---------------------------------------------");
        linesLogger.doLog("time： " + formatTime(System.currentTimeMillis()));
        linesLogger.doLog("status： " + Integer.toString(responseCopier.getStatus()));
        linesLogger.doLog("Headers:");
        for (Header header : responseCopier.getHeaders()) {
            linesLogger.doLog(header.getName(), header.getValue());
        }
        linesLogger.doLog("Body:");
        linesLogger.doLog(new String(responseCopier.getBytes()));
        linesLogger.end();
    }

    private String formatTime(Long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,S");
        return format.format(timestamp);
    }

    private void writeResponse(ServletResponse response, HttpServletResponseCopier responseCopier) throws IOException {
        if (responseCopier.isUseWriter()) {
            PrintWriter out = response.getWriter();
            out.write(responseCopier.getWriterChars());
            out.flush();
        } else {
            OutputStream out = response.getOutputStream();
            out.write(responseCopier.getStreamBytes());
            out.flush();
        }
    }

    private Level shouldDump(HttpRequestCopier requestCopier, HttpServletResponseCopier responseCopier) {
        if (isExcludedUri(requestCopier.getRequestURI(), requestCopier.getServletPath())) {
            return null;
        }
        String dumpAll = requestCopier.getParameter(DUMP_ALL_PARAM);
        if (Strings.hasText(dumpAll)) {
            this.dumpAll = Boolean.parseBoolean(dumpAll);
        }
        if (400 > responseCopier.getStatus()) {
            return Level.INFO;
        } else if (400 <= responseCopier.getStatus()) {
            return Level.WARN;
        } else if (500 <= responseCopier.getStatus()) {
            return Level.ERROR;
        }
        return null;
    }

    private boolean isExcludedUri(String requestURI, String servletPath) {
        if (requestURI.length() > servletPath.length()) {
            requestURI = requestURI.substring(servletPath.length());
        }
        return excludedUriRoutes.contains(requestURI);
    }

    class LinesLogger {
        private final StringBuilder lines = new StringBuilder(256);
        private final Level dumpLevel;

        public LinesLogger(Level dumpLevel) {
            this.dumpLevel = dumpLevel;
        }

        void start() {
            lines.append("\n");
            lines.append("====================================================");
            lines.append("\n");
        }
        void doLog(String str) {
            lines.append(limitLength(str));
            lines.append("\n");
        }
        void doLog(String attribute, String value) {
            lines.append("    ");
            lines.append(limitLength(attribute));
            lines.append(": ");
            lines.append(limitLength(value));
            lines.append("\n");
        }
        void end() {
            lines.append("====================================================");
            String linestr = lines.toString();
            switch (dumpLevel) {
                case TRACE:
                    logger.trace(linestr);
                    break;
                case DEBUG:
                    logger.debug(linestr);
                    break;
                case INFO:
                    logger.info(linestr);
                    break;
                case WARN:
                    logger.warn(linestr);
                    break;
                case ERROR:
                    logger.error(linestr);
                    break;
                default:
                    break;
            }
        }
    }

    private String limitLength(String str) {
        final int maxLength = 512;
        if (null == str) {
            return "";
        } else {
            // set max log item length to 512
            return (str.length() > maxLength) ?str.substring(0, maxLength) : str;
        }
    }

    @Override
    public void destroy() {
        // empty
    }
}
