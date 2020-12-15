package lk.ijse.dep.web.api;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "OrderServlet",urlPatterns = "/orders")
public class OrdersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");

        try(Connection connection = cp.getConnection()) {
             PreparedStatement pstm = connection.prepareStatement("SELECT * FROM orders");
             ResultSet rst = pstm.executeQuery();

             while(rst.next()){
                 String id = rst.getString(1);
                 String date= rst.getString(2);
                 String customerId=rst.getString(3);

             }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
}
