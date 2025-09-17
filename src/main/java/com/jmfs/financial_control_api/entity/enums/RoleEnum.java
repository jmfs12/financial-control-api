package com.jmfs.financial_control_api.entity.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");
    private final String value;
    RoleEnum(String value) {
        this.value = value;
    }

    public static RoleEnum fromString(String role) {
        for(RoleEnum roleEnum : RoleEnum.values()) {
            if(roleEnum.value.equals(role)) {
                return roleEnum;
            }
        }
        throw new IllegalArgumentException("Invalid Role " + role);
    }
}
