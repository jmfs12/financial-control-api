package com.jmfs.financial_control_api.entity.enums;

public enum StatusEnum {
    ACTIVE("active"),
    INACTIVE("inactive"),
    DELETED("deleted");

    private String status;

    StatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
