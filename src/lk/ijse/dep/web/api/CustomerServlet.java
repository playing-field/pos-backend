package lk.ijse.dep.web.api;

import jakarta.json.Json;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParsingException;
import lk.ijse.dep.web.model.Customer;
import org.apache.commons.dbcp2.BasicDataSource;

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

@WebServlet(name = "CustomerServlet", urlPatterns = "/customers")
public class CustomerServlet extends HttpServlet {
//    @Override
//    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//        resp.setHeader("Access-Control-Allow-Headers", "Content-type");
//        resp.setHeader("Access-Control-Allow-Methods","GET,PUT,POST,DELETE");
//    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");


        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");


        try(Connection connection = cp.getConnection();) {
            Customer customer;
            if(request.getContentType().equals("application/json")){
                BufferedReader reader = request.getReader();
                Jsonb jsonb = JsonbBuilder.create();
                 customer = jsonb.fromJson(reader, Customer.class);
            }else{
                /*application/x-www-form-url-encoded*/
                String id = request.getParameter("id");
                String name=request.getParameter("name");
                String address= request.getParameter("address");
                customer= new Customer(id,name,address);
                System.out.println(request.getContentType());
                System.out.println(customer);
            }




            if (customer.getId() == null || customer.getName() == null || customer.getAddress() == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (!customer.getId().matches("C\\d{3}") || customer.getName().trim().isEmpty() || customer.getAddress().trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }


            String id = customer.getId();
            String name = customer.getName();
            String address = customer.getAddress();

            PreparedStatement pstm = connection.prepareStatement("INSERT INTO Customer VALUES (?,?,?)");
            pstm.setObject(1, id);
            pstm.setObject(2, name);
            pstm.setObject(3, address);

            if (pstm.executeUpdate() > 0) {

                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //mehtnadi enne mkk hari wela customer table ke anathhtn internma server eroor ekk denwa
            }


        }catch (SQLIntegrityConstraintViolationException exception){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);  //inna customer id ekeata ekk ewwoth me exception eka paninwa

        } catch (SQLException throwables) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throwables.printStackTrace();

        } catch (JsonbException ex){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");

        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");

        response.setContentType("application/json");
//        response.addHeader("Access-Control-Allow-Origin","*");
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        try (Connection connection = cp.getConnection();) {

            PrintWriter out = response.getWriter();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Customer" + ((id != null) ? " WHERE id=?" : ""));
            if (id != null) {
                preparedStatement.setObject(1, id);
            }
            ResultSet rst = preparedStatement.executeQuery();


            List<Customer> customers = new ArrayList<>();


            while (rst.next()) {
                id = rst.getString(1);
                String name = rst.getString(2);
                String address = rst.getString(3);
                customers.add(new Customer(id, name, address));
            }

            if (id != null && customers.isEmpty()) {
//                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                Jsonb jsonb = JsonbBuilder.create();
                out.println(jsonb.toJson(customers));
            }


        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");


        String id = req.getParameter("id");
        if(id==null|| !id.matches("C\\d{3}")){   //delete karanna ona customege id eka check karala balanwa
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");

        try(Connection connection = cp.getConnection();) {
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Customer WHERE id=?");
            pstm.setObject(1, id);
            if (pstm.executeQuery().next()) {    //mehema customer knk innwanm witharak api eyawa delete karanna kiyanwa
                pstm = connection.prepareStatement("DELETE  FROM Customer WHERE id=?");
                pstm.setObject(1, id);
                if (pstm.executeUpdate() > 0) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);  //ehema customer knk naththn meha ekk yawanwa
            }
        }catch (SQLIntegrityConstraintViolationException ex){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);  //me customer wena order kekta sambanda nm ena awula kiyanwa
        } catch (SQLException throwables) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            throwables.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.addHeader("Access-Control-Allow-Origin","http://localhost:3000");




        String id = req.getParameter("id");
        if(id==null || !id.matches("C\\d{3}")){  //MEthandi blnwa id ekak thiyanwda kiyala check karala blanwa
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");


        try(Connection connection = cp.getConnection();) {
            Jsonb jsonb = JsonbBuilder.create();
            Customer customer = jsonb.fromJson(req.getReader(), Customer.class);

            /*Validation Logic*/
            if (customer.getId() != null || customer.getName() == null || customer.getAddress() == null) { //MEthandi blnwa name ekayi address ekayi null da kiyala saha id ekk json eka athule ewala thiyanwad kiyala
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (customer.getName().trim().isEmpty() || customer.getAddress().trim().isEmpty()) { //mehtnadi ena address name wala spaces witharak thiyana words da kiyala balnwa
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Customer WHERE id=?");
            pstm.setObject(1,id);
            if(pstm.executeQuery().next()){
                pstm = connection.prepareStatement("UPDATE Customer SET name=?, address=? WHERE id=?");
                pstm.setObject(1, customer.getName());
                pstm.setObject(2, customer.getAddress());
                pstm.setObject(3, id);
                if (pstm.executeUpdate() > 0) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);   //hariyata update unoth no content status ekk yawanwa
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }else{
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }







        } catch (SQLException throwables) {
            throwables.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }catch (JsonbException ex){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //methanadi json eka waradi hariyata convert karaganna bariunoth exception ekk paninwa ekata error ekk yawanwa
        }
    }
}
