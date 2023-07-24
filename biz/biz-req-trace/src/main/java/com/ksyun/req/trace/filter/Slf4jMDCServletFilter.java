package com.ksyun.req.trace.filter;

import com.ksyun.req.trace.ReqTraceConsts;
import com.ksyun.req.trace.RequestTraceContextSlf4jMDCHolder;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MimeHeaders;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 该Servlet 过滤器在请求进入和离开时设置和清空请求 Trace ID；
 * 在请求进入时调用initContextHolders()方法设置Trace ID，然后在请求离开时调用resetContextHolders()方法清空Trace ID
 * Trace ID 在 MDC 中
 *
 * @author ksc
 */
public class Slf4jMDCServletFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(Slf4jMDCServletFilter.class);

    private static final List<String> FILTER_URLS = new ArrayList<>();

    //在过滤器初始化时从配置中获取 filterUrl 参数，并将其保存到 FILTER_URLS 中，
    // 以便在 doFilterInternal() 方法中进行判断
    @Override
    protected void initFilterBean() throws ServletException {
        String filterUrlStr = this.getFilterConfig().getInitParameter("filterUrl");
        if (StringUtils.isNotBlank(filterUrlStr)) {
            log.debug("发现配置过滤url参数:{}" , filterUrlStr);
            String[] urlArray = filterUrlStr.split(";");
            FILTER_URLS.addAll(Arrays.asList(urlArray));
        }
    }

    /**
     * 异步处理可能会丢失数据
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (!FILTER_URLS.contains(request.getRequestURI())) {

            initContextHolders(request); //设置Trace ID
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            resetContextHolders();  //清除Trace ID
        }
    }

    /**
     * 获取或构造请求参数，默认是请求id和父类名称以及时间
     */
    private void initContextHolders(HttpServletRequest request) {
        String requestId = request.getHeader(ReqTraceConsts.REQUEST_ID); //先从HTTP Header中获取X-KSY-REQUEST-ID
//        System.out.println("requestId 过滤器中= " + requestId);
        if (StringUtils.isBlank(requestId)) {
            requestId = UUID.randomUUID().toString(); // 如果请求中没有 Trace ID，则生成一个新的 Trace ID
//            System.out.println("requestId 过滤器中 生成的随机的= " + requestId); //设置到TRACE_KEY
        }
        //将 Trace ID 设置到 MDC（Mapped Diagnostic Context）中，以便在日志中输出 Trace ID
        MDC.put(ReqTraceConsts.TRACE_KEY, requestId);
    }

    //将 Trace ID 从 MDC 中清除，以免 Trace ID 被错误地复用。
    private void resetContextHolders() {
        MDC.remove(ReqTraceConsts.TRACE_KEY);
    }



}
