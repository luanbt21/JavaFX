package sample;

public class NewOrder {
    private String id;
    private String description;
    private String price;
    private String date;
    private int quantity;
    private String tableName;

    public String getDate() {
        return date;
    }

    public NewOrder(String id, String date, String total) {
        this.id = id;
        this.date = date;
        this.total = total;
    }

    public NewOrder(String description, String price, int quantity, String total) {
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
    }

    public String getTableName() {
        return tableName;
    }

    private String total;

    public NewOrder(String id, String description, String price, String date, int quantity, String total) {
        this.id = id;
        this.price = price;
        this.description = description;
        this.date = date;
        this.quantity = quantity;
        this.total = total;
    }

    public NewOrder(String id, String tableName, String date, String total) {
        this.id = id;
        this.date = date;
        this.total = total;
        this.tableName = tableName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getTotal() {
        return total;
    }


}
