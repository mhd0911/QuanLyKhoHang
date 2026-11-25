package vn.edu.quanlykho;

import java.math.BigInteger;

public final class Product {
    public static final int LOW_STOCK_THRESHOLD = 5;

    public final int id;
    public final String name;
    public final int price;
    public final int quantity;
    public final String category;
    public final boolean active;
    public final String description;

    public Product(int id, String name, int price, int quantity, String category,
                   boolean active, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    public boolean isLowStock() {
        return quantity < LOW_STOCK_THRESHOLD;
    }

    public BigInteger stockValue() {
        return BigInteger.valueOf((long) price * quantity);
    }
}
