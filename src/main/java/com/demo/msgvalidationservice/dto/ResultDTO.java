package com.demo.msgvalidationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultDTO {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private String data;

    // Default constructor
    public ResultDTO() {}

    // Constructor with parameters
    public ResultDTO(boolean success,String data, String message) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
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

    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }

}
