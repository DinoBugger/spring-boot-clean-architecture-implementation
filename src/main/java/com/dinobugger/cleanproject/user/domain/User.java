package com.dinobugger.cleanproject.user.domain;

public record User(
    Long id,
    String firstName,
    String lastName,
    String username,
    String email,
    UserStatus status
) {
}

