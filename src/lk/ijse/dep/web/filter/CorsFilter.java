package lk.ijse.dep.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebFilter(filterName = "CorsFilter", urlPatterns = "/*")
//public class CorsFilter implements Filter {
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletResponse response=(HttpServletResponse) servletResponse;
//
//
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//        response.setHeader("Access-Control-Allow-Headers", "Content-type");
//        response.setHeader("Access-Control-Allow-Methods","GET,PUT,POST,DELETE");
//        System.out.println("CorsFilter Incoming ");
//        filterChain.doFilter(servletRequest,servletResponse);
//        System.out.println("CorsFilter Outgoing");
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}
@WebFilter(filterName = "CorsFilter",urlPatterns = "/*")
public class CorsFilter extends HttpFilter{
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Headers", "Content-type");
        response.setHeader("Access-Control-Allow-Methods","GET,PUT,POST,DELETE");
        chain.doFilter(request,response);
    }
}
