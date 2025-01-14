package com.eshop.client.model;

public class Select2Model {
    private String id;
    private String text;
    private String image;

    public Select2Model(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public Select2Model(String id, String text, String image) {
        this.id = id;
        this.text = text;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
