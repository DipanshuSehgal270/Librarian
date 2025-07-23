package com.example.Book.Management.System.entity;

public enum UserRole {
    USER, ADMIN, LIBRARIAN;

    public String getAuthority()
    {
        return "ROLE_" + this.name();
    }
}
