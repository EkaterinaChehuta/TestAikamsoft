package org.example.testAikamsoft.domain;

import javax.persistence.*;

@Entity
@Table(schema = "json", name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String productName;
    private double price;

    public Product() {
    }

    public Product(String productName, double price) {
        this.productName = productName;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public void getProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
