package com.zayna.user.enums;

public enum Role {
    ADMIN,
    ORGANIZER,
    CLIENT;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}