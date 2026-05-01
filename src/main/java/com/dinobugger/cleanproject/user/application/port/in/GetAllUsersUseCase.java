package com.dinobugger.cleanproject.user.application.port.in;

import com.dinobugger.cleanproject.user.domain.User;
import java.util.List;

public interface GetAllUsersUseCase {

  List<User> getAllUsers();
}

