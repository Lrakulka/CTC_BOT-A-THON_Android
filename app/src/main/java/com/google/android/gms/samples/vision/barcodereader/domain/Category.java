package com.google.android.gms.samples.vision.barcodereader.domain;

/**
 * @author Victor Letunovsky (@vletunovsky)
 */

public class Category {

    private Long id;

    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
