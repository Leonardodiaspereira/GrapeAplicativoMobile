package com.example.grapeapp.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    private boolean success;
    private String message;

    @SerializedName("id")
    private int id;

    // Getters e Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
