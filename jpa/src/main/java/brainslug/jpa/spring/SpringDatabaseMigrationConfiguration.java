package brainslug.jpa.spring;

import brainslug.jpa.migration.FlywayMigration;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringDatabaseMigrationConfiguration {

  @Autowired
  DataSource dataSource;

  @Bean
  public FlywayMigration migration() {
    return new FlywayMigration();
  }

  @Bean
  public Flyway flyway() {
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    return flyway;
  }
}
