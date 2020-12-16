package lk.ijse.dep.web.api;

import com.sun.org.apache.xpath.internal.operations.Or;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.dep.web.model.Order;
import lk.ijse.dep.web.model.OrderInformation;
import lk.ijse.dep.web.model.OrderItems;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "OrderServlet",urlPatterns = "/orders")
public class OrdersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");

        try(Connection connection = cp.getConnection()) {
             PreparedStatement pstm = connection.prepareStatement("SELECT * FROM orders");
             ResultSet rst = pstm.executeQuery();
            List<Order> orderList=new ArrayList<>();
             while(rst.next()){
                 String id = rst.getString(1);
                 String date= rst.getString(2);
                 String customerId=rst.getString(3);
                 orderList.add(new Order(id,date,customerId));

             }

            Jsonb jsonb = JsonbBuilder.create();
             String json = jsonb.toJson(orderList);
            resp.setContentType("application/json");
            resp.getWriter().println(json);




        } catch (SQLException throwables) {
            throwables.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        Jsonb jsonb=JsonbBuilder.create();
        String line;
        String json;
        json=reader.readLine();
        while((line=reader.readLine())!=null){
            json+=line;
        }

        OrderInformation orderInformation = jsonb.fromJson(json, OrderInformation.class);

        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        try(Connection connection = cp.getConnection()) {
//            connection.setAutoCommit(false);
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO orders VALUES (?,?,?)");
            pstm.setObject(1,orderInformation.getId());
            pstm.setObject(2,orderInformation.getDate());
            pstm.setObject(3,orderInformation.getCustomerid());

            if(pstm.executeUpdate()>0){
                List<OrderItems> orderItems = orderInformation.getOrderItems();

                for(int i = 0; i< orderItems.size(); i++) {
                    pstm=connection.prepareStatement("INSERT INTO orderdetail VALUES (?,?,?,?)");

                    pstm.setObject(1, orderItems.get(i).getOrderId());
                    pstm.setObject(2, orderItems.get(i).getItemCode());
                    pstm.setObject(3, orderItems.get(i).getQty());
                    pstm.setObject(4, orderItems.get(i).getUnitPrice());

                    if(pstm.executeUpdate()>0){
                        pstm=connection.prepareStatement("UPDATE Item SET qtyOnHand=qtyOnHand-? WHERE code=?");
                        pstm.setObject(1,orderItems.get(i).getQty());
                        pstm.setObject(2,orderItems.get(i).getItemCode());
                        if(pstm.executeUpdate()>0){
                            continue;
                        }else{
                            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return;
                        }
                    }else{
                        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return;
                    }
                }
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }else{
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

//            connection.setAutoCommit(true);

        } catch (SQLException throwables) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throwables.printStackTrace();
        }


    }
}
