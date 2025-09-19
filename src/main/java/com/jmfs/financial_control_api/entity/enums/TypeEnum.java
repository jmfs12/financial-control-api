package com.jmfs.financial_control_api.entity.enums;

import lombok.Getter;

@Getter
public enum TypeEnum {
    CHECKING("checking"),
    SAVINGS("savings"),
    CREDIT("credit");

    private final String value;

    TypeEnum(String value) {
        this.value = value;
    }

}
