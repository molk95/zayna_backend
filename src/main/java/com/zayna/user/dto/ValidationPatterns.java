package com.zayna.user.dto;

public final class ValidationPatterns {

    private ValidationPatterns() {
    }

    public static final String USERNAME = "^[A-Za-z0-9._-]{3,30}$";
    public static final String USERNAME_MESSAGE =
            "Username must be 3-30 characters (letters, digits, '.', '_' or '-')";

    public static final String PASSWORD =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,64}$";
    public static final String PASSWORD_MESSAGE =
            "Password must be 8-64 characters with an uppercase letter, a lowercase letter, a digit and a special character";

    public static final String PHONE = "^\\+?[0-9 ]{8,20}$";
    public static final String PHONE_MESSAGE = "Invalid phone number";

    public static final String URL = "^https?://\\S{3,250}$";
    public static final String URL_MESSAGE = "URL must start with http:// or https://";
}