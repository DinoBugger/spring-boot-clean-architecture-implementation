package com.dinobugger.cleanproject.user.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
}

