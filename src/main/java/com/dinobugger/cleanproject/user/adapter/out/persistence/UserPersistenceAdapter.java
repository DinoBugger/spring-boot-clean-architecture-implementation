package com.dinobugger.cleanproject.user.adapter.out.persistence;

import com.dinobugger.cleanproject.user.application.port.out.LoadUsersPort;
import com.dinobugger.cleanproject.user.domain.User;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class UserPersistenceAdapter implements LoadUsersPort {

  private final SpringDataUserJpaRepository userJpaRepository;

  public UserPersistenceAdapter(SpringDataUserJpaRepository userJpaRepository) {
    this.userJpaRepository = userJpaRepository;
  }

  @Override
  public List<User> findAllUsers() {
    return userJpaRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
        .map(this::toDomain)
        .toList();
  }

  private User toDomain(UserJpaEntity entity) {
    return new User(
        entity.getId(),
        entity.getFirstName(),
        entity.getLastName(),
        entity.getUsername(),
        entity.getEmail(),
        entity.getStatus()
    );
  }
}

