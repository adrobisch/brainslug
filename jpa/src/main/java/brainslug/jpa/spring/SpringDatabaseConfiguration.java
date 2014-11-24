package brainslug.jpa.spring;

import brainslug.jpa.Database;
import com.mysema.query.jpa.JPQLTemplates;
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

  @PersistenceContext(unitName = "bs")
  EntityManager entityManager;

  @Autowired
  JPQLTemplates jpqlTemplates;

  @Bean
  public Database database() {
    return new Database(entityManager, jpqlTemplates);
  }

}
