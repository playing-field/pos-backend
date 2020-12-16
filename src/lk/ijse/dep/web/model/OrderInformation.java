package lk.ijse.dep.web.model;

import java.util.List;

public class OrderInformation {
    private String id;
    private String date;
    private String customerid;
    private List<OrderItems> orderItems;

    public OrderInformation() {
    }

    @Override
    public String toString() {
        return "OrderInformation{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", customerid='" + customerid + '\'' +
                ", orderItems=" + orderItems +
                '}';
    }

    public OrderInformation(String id, String date, String customerid, List<OrderItems> orderItems) {
        this.setId(id);
        this.setDate(date);
        this.setCustomerid(customerid);
        this.setOrderItems(orderItems);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public List<OrderItems> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItems> orderItems) {
        this.orderItems = orderItems;
    }
}
