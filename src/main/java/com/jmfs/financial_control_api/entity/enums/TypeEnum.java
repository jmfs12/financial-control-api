package com.jmfs.financial_control_api.entity.enums;

import lombok.Getter;

@Getter
public enum TypeEnum {
    CHECKING("checking"),
    SAVINGS("savings"),
    CREDIT("credit");

    private final String value;

    public static TypeEnum fromString(String value) {
        for (TypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new  IllegalArgumentException("Invalid type " + value);
    }

    TypeEnum(String value) {
        this.value = value;
    }

}
