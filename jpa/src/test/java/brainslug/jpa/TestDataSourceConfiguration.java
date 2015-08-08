package brainslug.jpa;

import org.h2.jdbcx.JdbcDataSource;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.XADataSource;

@Configuration
public class TestDataSourceConfiguration {

  enum Database {
    POSTGRES,
    H2
  }

  @Bean
  @Qualifier("brainslug")
  public XADataSource xaDataSource() {
    switch(getDb()) {
      case POSTGRES:
        return createPostgresDatasource();
      default:
        return createH2Datasource();
    }
  }

  private XADataSource createPostgresDatasource() {
    PGXADataSource pgxaDataSource = new PGXADataSource();
    pgxaDataSource.setUrl(getJdbcUrl());
    pgxaDataSource.setUser(getDbUser());
    pgxaDataSource.setPassword(getDbPassword());
    return pgxaDataSource;
  }

  private XADataSource createH2Datasource() {
    JdbcDataSource jdbcDataSource = new JdbcDataSource();
    jdbcDataSource.setURL(getJdbcUrl());
    jdbcDataSource.setUser(getDbUser());
    jdbcDataSource.setPassword(getDbPassword());
    return jdbcDataSource;
  }

  String getDbPassword() {
    String passwordFromProperties = System.getProperty("db.password");
    return passwordFromProperties == null ? "" : passwordFromProperties;
  }

  String getDbUser() {
    String userFromProperties = System.getProperty("db.user");
    return userFromProperties == null ? "sa" : userFromProperties;
  }

  String getJdbcUrl() {
    String jdbcUrlFromProperties = System.getProperty("db.jdbc.url");
    return jdbcUrlFromProperties == null ? "jdbc:h2:mem:testdb" : jdbcUrlFromProperties;
  }

  Database getDb() {
    String db = System.getProperty("db");
    return Database.valueOf(db == null ? "H2" : db.toUpperCase());
  }

}
