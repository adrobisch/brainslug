package brainslug.jpa.spring;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.icatch.jta.hibernate3.TransactionManagerLookup;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.util.Properties;

@Configuration
public class SpringJtaConfiguration {

  @Autowired
  XADataSource xaDataSource;

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManager() throws Throwable  {
    LocalContainerEntityManagerFactoryBean entityManager =
      new LocalContainerEntityManagerFactoryBean();
    entityManager.setJtaDataSource(dataSource());
    Properties properties = new Properties();
    properties.setProperty("hibernate.transaction.manager_lookup_class",
      TransactionManagerLookup.class.getName());
    entityManager.setJpaProperties(properties);
    return entityManager;
  }

  @Bean
  public DataSource dataSource() {
    AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();

    atomikosDataSourceBean.setXaDataSource(xaDataSource);
    atomikosDataSourceBean.setUniqueResourceName("ds");
    atomikosDataSourceBean.setMaxPoolSize(50);

    return atomikosDataSourceBean;
  }

  @Bean
  public UserTransaction userTransaction() throws Throwable {
    UserTransactionImp userTransactionImp = new UserTransactionImp();
    userTransactionImp.setTransactionTimeout(8000);
    return userTransactionImp;
  }

  @Bean(destroyMethod = "close")
  public TransactionManager transactionManager() throws Throwable {
    UserTransactionManager userTransactionManager = new UserTransactionManager();
    userTransactionManager.setForceShutdown(false);
    userTransactionManager.init();

    return userTransactionManager;
  }

  @Bean
  public PlatformTransactionManager platformTransactionManager()  throws Throwable {
    return new JtaTransactionManager(userTransaction(), transactionManager());
  }
}