package brainslug.jdbc;

import com.mysema.query.jpa.HQLTemplates;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
@EnableTransactionManagement
@Import(SpringJtaConfiguration.class)
public class SpringDatabaseConfiguration {

  @Autowired
  JdbcDataSource dataSource;

  @PersistenceContext
  EntityManager entityManager;

  @Bean
  public Migration migration() {
    return new Migration();
  }

  @Bean
  public Flyway flyway() {
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    return flyway;
  }

  @Bean
  public Database database() {
    return new Database(entityManager, new HQLTemplates());
  }

}
