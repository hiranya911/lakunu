package org.lakunu.web;

import com.google.common.base.Strings;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkArgument;

@WebFilter(urlPatterns = "/*")
public class HttpHiddenMethodFilter implements Filter {

    private static final String ALREADY_FILTERED = HttpHiddenMethodFilter.class.getName();
    private static final String METHOD_PARAM = "_method";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Inspired by the Spring's HttpHiddenMethodFilter class
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        Object alreadyFiltered = request.getAttribute(ALREADY_FILTERED);
        if (alreadyFiltered != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            checkArgument(request instanceof HttpServletRequest, "unsupported request");
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String paramValue = request.getParameter(METHOD_PARAM);
            if ("POST".equals(httpRequest.getMethod()) && !Strings.isNullOrEmpty(paramValue)) {
                String method = paramValue.toUpperCase(Locale.ENGLISH);
                HttpServletRequest wrapper = new HttpMethodRequestWrapper(httpRequest, method);
                filterChain.doFilter(wrapper, response);
            } else {
                filterChain.doFilter(request, response);
            }
        } finally {
            request.setAttribute(ALREADY_FILTERED, Boolean.TRUE);
        }
    }

    @Override
    public void destroy() {
    }

    private final static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {

        private final String method;

        private HttpMethodRequestWrapper(HttpServletRequest request, String method) {
            super(request);
            checkArgument(!Strings.isNullOrEmpty(method), "method is required");
            this.method = method;
        }

        @Override
        public String getMethod() {
            return method;
        }
    }
}
