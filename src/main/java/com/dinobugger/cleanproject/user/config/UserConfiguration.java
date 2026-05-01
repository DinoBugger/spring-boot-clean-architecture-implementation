package com.dinobugger.cleanproject.user.config;

import com.dinobugger.cleanproject.user.application.port.in.GetAllUsersUseCase;
import com.dinobugger.cleanproject.user.application.port.out.LoadUsersPort;
import com.dinobugger.cleanproject.user.application.usecase.GetAllUsersService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfiguration {

  @Bean
  public GetAllUsersUseCase getAllUsersUseCase(LoadUsersPort loadUsersPort) {
    return new GetAllUsersService(loadUsersPort);
  }
}

