package com.dinobugger.cleanproject.user.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dinobugger.cleanproject.user.application.port.out.LoadUsersPort;
import com.dinobugger.cleanproject.user.domain.User;
import com.dinobugger.cleanproject.user.domain.UserStatus;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GetAllUsersServiceTest {

  @Test
  void getAllUsers_shouldReturnUsersFromPort() {
    LoadUsersPort loadUsersPort = Mockito.mock(LoadUsersPort.class);
    List<User> expectedUsers = List.of(
        new User(1L, "John", "Doe", "jdoe", "john.doe@example.com", UserStatus.ACTIVE),
        new User(2L, "Jane", "Smith", "jsmith", "jane.smith@example.com", UserStatus.ACTIVE)
    );
    when(loadUsersPort.findAllUsers()).thenReturn(expectedUsers);

    GetAllUsersService service = new GetAllUsersService(loadUsersPort);

    List<User> actualUsers = service.getAllUsers();

    assertThat(actualUsers).isEqualTo(expectedUsers);
    verify(loadUsersPort).findAllUsers();
  }
}

