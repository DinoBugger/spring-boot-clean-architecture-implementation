package com.dinobugger.cleanproject.user.adapter.in.web;

import com.dinobugger.cleanproject.user.application.port.in.GetAllUsersUseCase;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final GetAllUsersUseCase getAllUsersUseCase;

  public UserController(GetAllUsersUseCase getAllUsersUseCase) {
    this.getAllUsersUseCase = getAllUsersUseCase;
  }

  @GetMapping
  public List<UserResponse> getAllUsers() {
    return getAllUsersUseCase.getAllUsers().stream()
        .map(UserResponse::from)
        .toList();
  }
}

