package com.dinobugger.cleanproject.database;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class IntegrationDatabaseTest {
  @Autowired
  private JdbcTemplate jdbcTemplate;


  @Test
  void testConnection() {
    Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
    assertThat(result).isEqualTo(1);
    System.out.println("Connect to PostgresSQL successful");
  }

}
