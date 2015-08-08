package brainslug.jpa.spring;

import brainslug.jpa.migration.DatabaseMigration;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringDatabaseMigrationConfiguration {

  @Autowired
  DataSource dataSource;

  @Bean
  Liquibase liquibase() {
    try {
      return new Liquibase("brainslug/database/migration/update.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(dataSource.getConnection()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Bean
  public DatabaseMigration migration() {
    return new DatabaseMigration(liquibase());
  }
}
