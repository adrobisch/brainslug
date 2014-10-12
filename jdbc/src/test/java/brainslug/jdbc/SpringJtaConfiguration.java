package brainslug.jdbc;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.icatch.jta.hibernate3.TransactionManagerLookup;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.util.Properties;

public class SpringJtaConfiguration {
  @Bean
  public LocalContainerEntityManagerFactoryBean entityManager() throws Throwable  {
    LocalContainerEntityManagerFactoryBean entityManager =
      new LocalContainerEntityManagerFactoryBean();
    entityManager.setDataSource(dataSource());
    Properties properties = new Properties();
    properties.setProperty("hibernate.transaction.manager_lookup_class",
      TransactionManagerLookup.class.getName());
    entityManager.setJpaProperties(properties);
    return entityManager;
  }

  @Bean
  public DataSource dataSource() {
    AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();

    atomikosDataSourceBean.setXaDataSource(xaDataSource());
    atomikosDataSourceBean.setUniqueResourceName("ds");
    atomikosDataSourceBean.setMaxPoolSize(10);
    return atomikosDataSourceBean;
  }

  @Bean
  public JdbcDataSource xaDataSource() {
    JdbcDataSource jdbcDataSource = new JdbcDataSource();
    jdbcDataSource.setURL("jdbc:h2:mem:testdb");
    jdbcDataSource.setUser("sa");
    jdbcDataSource.setPassword("");
    return jdbcDataSource;
  }

  @Bean
  public UserTransaction userTransaction() throws Throwable {
    UserTransactionImp userTransactionImp = new UserTransactionImp();
    userTransactionImp.setTransactionTimeout(5000);
    return userTransactionImp;
  }

  @Bean(initMethod = "init", destroyMethod = "close")
  public TransactionManager transactionManager() throws Throwable {
    UserTransactionManager userTransactionManager = new UserTransactionManager();
    userTransactionManager.setForceShutdown(false);
    return userTransactionManager;
  }

  @Bean
  public PlatformTransactionManager platformTransactionManager()  throws Throwable {
    JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(
      userTransaction(), transactionManager());
    return jtaTransactionManager;
  }
}