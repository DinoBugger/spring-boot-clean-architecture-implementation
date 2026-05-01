package com.dinobugger.cleanproject.user.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UserControllerIntegrationTest {

  @Autowired
  private UserController userController;

  @Test
  void getAllUsers_shouldReturnSeededUsers() {
    var users = userController.getAllUsers();

    assertThat(users)
        .hasSize(3)
        .extracting(UserResponse::username)
        .containsExactly("jdoe", "jsmith", "btaylor");

    assertThat(users)
        .extracting(UserResponse::status)
        .containsExactly("ACTIVE", "ACTIVE", "INACTIVE");
  }
}



