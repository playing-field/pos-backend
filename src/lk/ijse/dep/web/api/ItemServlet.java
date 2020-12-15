package lk.ijse.dep.web.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import lk.ijse.dep.web.model.Customer;
import lk.ijse.dep.web.model.Item;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ItemServlet", urlPatterns = "/items")
public class ItemServlet extends HttpServlet {
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//        resp.setHeader("Access-Control-Allow-Headers", "Content-type");
//        resp.setHeader("Access-Control-Allow-Methods","GET,PUT,POST,DELETE");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//        response.setContentType("application/json"); //meka danna epa jquery ge ajax eka use karankota khomath wada na. jquery ge ajax use karankota u mula wenwa....!!!


        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");


        try (Connection connection = cp.getConnection();){

            Jsonb jsonb = JsonbBuilder.create();
            Item item = jsonb.fromJson(reader, Item.class);

            if(item.getCode()==null ||item.getDescription()==null|| item.getQtyOnHand()==0||item.getUnitPrice()==0){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if(!item.getCode().matches("P\\d{3}")||item.getDescription().trim().isEmpty()){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }


            PreparedStatement pstm = connection.prepareStatement("INSERT INTO Item VALUES (?,?,?,?)");

            pstm.setObject(1, item.getCode());
            pstm.setObject(2, item.getDescription());
            pstm.setObject(3, item.getQtyOnHand());
            pstm.setObject(4, item.getUnitPrice());
            if (pstm.executeUpdate() > 0) {
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }catch (SQLIntegrityConstraintViolationException exception){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }catch (JsonbException ex){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*Cors Policy*/
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        String code = request.getParameter("code");
//        response.setContentType("application/xml");
        response.setContentType("application/json");
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");

        try (Connection connection = cp.getConnection();) {

            PrintWriter out = response.getWriter();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Item" + ((code != null) ? " WHERE code=?" : ""));

            if (code != null) {
                preparedStatement.setObject(1, code);
            }

            ResultSet rst = preparedStatement.executeQuery();

            List<Item> itemList = new ArrayList<>();


            while (rst.next()) {
                code = rst.getString(1);
                String description = rst.getString(2);
                double unitPrice = rst.getDouble(3);
                int qtyOnHand = rst.getInt(4);

                itemList.add(new Item(code, description, unitPrice, qtyOnHand));


            }


            if(code!=null && itemList.isEmpty()){
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);

            }else{
                Jsonb jsonb = JsonbBuilder.create();
                out.println(jsonb.toJson(itemList));
            }






        } catch (SQLException throwables) {
            throwables.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.addHeader("Access-Control-Allow-Origin","http://localhost:3000");


        String code = req.getParameter("code");
        if(code==null || !code.matches("P\\d{3}")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        try(Connection connection = cp.getConnection();) {
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Item WHERE code=?");
            pstm.setObject(1, code);

            if (pstm.executeQuery().next()) {
                pstm = connection.prepareStatement("DELETE FROM Item WHERE code=?");
                pstm.setObject(1, code);
                if (pstm.executeUpdate() > 0) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }


        }catch (SQLIntegrityConstraintViolationException ex){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException throwables) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throwables.printStackTrace();
        }

    }
}
