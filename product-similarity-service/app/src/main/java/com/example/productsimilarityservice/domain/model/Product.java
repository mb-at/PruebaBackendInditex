package com.example.productsimilarityservice.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {
    
    private String id;
    private String name;
    private Double price;
    private boolean availability;

    public Product() {
    }

    public Product(String id, String name, Double price, boolean availability) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availability = availability;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("price")
    public Double getPrice() {
        return price;
    }

    @JsonProperty("price")
    public void setPrice(Double price) {
        this.price = price;
    }

    
    @JsonProperty("availability")
    public Boolean getAvailability() {
        return availability;
    }

    @JsonProperty("availability")
    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }
}
