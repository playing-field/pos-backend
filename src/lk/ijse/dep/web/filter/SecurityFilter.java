package lk.ijse.dep.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

//@WebFilter(filterName = "SedurityFilter",urlPatterns = "/*")
@WebFilter(filterName = "SedurityFilter")
public class SecurityFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("Security Filter");
        filterChain.doFilter(servletRequest,servletResponse);
        System.out.println("Security Filter Outgoing");
    }

    @Override
    public void destroy() {

    }
}
