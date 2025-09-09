package com.jmfs.financial_control_api.entity.enums;

public enum RoleEnum {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");
    private String value;
    RoleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
