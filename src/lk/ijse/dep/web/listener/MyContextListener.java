package lk.ijse.dep.web.listener;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebListener
public class MyContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        BasicDataSource bds = new BasicDataSource();
        bds.setUsername("root");
        bds.setPassword("mysql");
        bds.setUrl("jdbc:mysql://localhost:3306/dep6");
        bds.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        bds.setConnectionProperties("org.apache.jasper.servlet.TldScanner.level = SEVERE");
        bds.setInitialSize(5);
        bds.setMaxTotal(5);


        ServletContext ctx = sce.getServletContext();
        ctx.setAttribute("cp",bds);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext scx = sce.getServletContext();
        BasicDataSource cp = (BasicDataSource) scx.getAttribute("cp");
        try {
            cp.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
