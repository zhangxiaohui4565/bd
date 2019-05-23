package com.gupao.bd.eos.client.filter;

import com.gupao.bd.eos.client.constants.Trace;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * 跨系统日志关联Filter
 */
public class TraceLogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest hrequest = (HttpServletRequest) request;
        // header包含TraceID，则只是传递
        String eosTraceId = hrequest.getHeader(Trace.TRACE_ID);
        if (null == eosTraceId) {
            eosTraceId = UUID.randomUUID().toString().replace("-","");
        }
        MDC.put(Trace.TRACE_ID, eosTraceId);
        HttpServletResponse hresponse = (HttpServletResponse) response;
        hresponse.setHeader(Trace.TRACE_ID, eosTraceId);
        try {
            chain.doFilter(request, response);
        } finally {
            // 清空MDC防止内存泄露
            MDC.clear();
        }
    }

    @Override
    public void destroy() {
        // no-op
    }
}
