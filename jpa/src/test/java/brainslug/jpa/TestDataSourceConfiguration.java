package brainslug.jpa;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestDataSourceConfiguration {
  @Bean
  @Qualifier("brainslug")
  public JdbcDataSource xaDataSource() {
    JdbcDataSource jdbcDataSource = new JdbcDataSource();
    jdbcDataSource.setURL("jdbc:h2:mem:testdb");
    jdbcDataSource.setUser("sa");
    jdbcDataSource.setPassword("");
    return jdbcDataSource;
  }
}
