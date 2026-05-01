package com.dinobugger.cleanproject.user.application.usecase;

import com.dinobugger.cleanproject.user.application.port.in.GetAllUsersUseCase;
import com.dinobugger.cleanproject.user.application.port.out.LoadUsersPort;
import com.dinobugger.cleanproject.user.domain.User;
import java.util.List;

public class GetAllUsersService implements GetAllUsersUseCase {

  private final LoadUsersPort loadUsersPort;

  public GetAllUsersService(LoadUsersPort loadUsersPort) {
    this.loadUsersPort = loadUsersPort;
  }

  @Override
  public List<User> getAllUsers() {
    return loadUsersPort.findAllUsers();
  }
}


