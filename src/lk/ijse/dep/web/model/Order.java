package lk.ijse.dep.web.model;

public class Order {
    private String id;
    private String date;
    private String customerId;


    public Order(String id, String date, String customerId) {
        this.setId(id);
        this.setDate(date);
        this.setCustomerId(customerId);
    }

    public Order() {
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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
