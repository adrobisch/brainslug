package brainslug.jpa.spring;

import brainslug.jpa.Database;
import brainslug.util.IdGenerator;
import brainslug.util.UuidGenerator;
import com.mysema.query.jpa.JPQLTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
@EnableTransactionManagement
public class SpringDatabaseConfiguration {

  @PersistenceContext
  @Qualifier("brainslug")
  EntityManager entityManager;

  @Autowired
  JPQLTemplates jpqlTemplates;

  @Bean
  Database database() {
    return new Database(entityManager, jpqlTemplates);
  }

  @Bean
  IdGenerator idGenerator() {
    return createIdGenerator();
  }

  protected IdGenerator createIdGenerator() {
    return new UuidGenerator();
  }

  @Bean
  SpringJpaPropertyStore springJpaPropertyStore() {
    return new SpringJpaPropertyStore(database(), idGenerator(), springJpaInstanceStore());
  }

  @Bean
  SpringJpaTokenStore springJpaTokenStore() {
    return new SpringJpaTokenStore(database(), idGenerator(), springJpaInstanceStore());
  }

  @Bean
  SpringJpaAsyncTriggerStore springJpaAsyncTriggerStore() {
    return new SpringJpaAsyncTriggerStore(database(), idGenerator());
  }

  @Bean
  SpringJpaInstanceStore springJpaInstanceStore() {
    return new SpringJpaInstanceStore(database(), idGenerator());
  }

}
