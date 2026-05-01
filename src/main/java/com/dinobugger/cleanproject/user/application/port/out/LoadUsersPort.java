package com.dinobugger.cleanproject.user.application.port.out;

import com.dinobugger.cleanproject.user.domain.User;
import java.util.List;

public interface LoadUsersPort {

  List<User> findAllUsers();
}

