package com.jmfs.financial_control_api.entity.enums;

public enum StatusEnum {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String status;

    StatusEnum(String status) {
        this.status = status;
    }

    public static StatusEnum fromString(String status) {
        for (StatusEnum statusEnum : values()) {
            if (statusEnum.status.equalsIgnoreCase(status)) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("Invalid Status " + status);
    }

    public String getValue() {
        return status;
    }
}
