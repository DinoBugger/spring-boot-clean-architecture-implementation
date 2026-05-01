package com.dinobugger.cleanproject.user.adapter.in.web;

import com.dinobugger.cleanproject.user.domain.User;

public record UserResponse(
    Long id,
    String firstName,
    String lastName,
    String username,
    String email,
    String status
) {

  public static UserResponse from(User user) {
    return new UserResponse(
        user.id(),
        user.firstName(),
        user.lastName(),
        user.username(),
        user.email(),
        user.status().name()
    );
  }
}

