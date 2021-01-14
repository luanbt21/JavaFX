package sample;

import java.sql.Blob;

public class Products {
    private int id;
    private String description;
    private String price;
    private String category;
    private Blob image;
    private String quantity;
    private String weight;

    public Products(int id, String description, String price, String category, String quantity, String weight, Blob image) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.category = category;
        this.image = image;
        this.quantity = quantity;
        this.weight = weight;
    }

    public Products(String description, String price, Blob image) {
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public String getWeight() {
        return weight;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public Blob getImage() {
        return image;
    }

    public String getQuantity() {
        return quantity;
    }
}

