package brainslug.jpa.spring;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysema.query.jpa.HQLTemplates;
import com.mysema.query.jpa.JPQLTemplates;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.util.Properties;

@Configuration
public class SpringHibernateConfiguration {

  @Autowired
  @Qualifier("brainslug")
  XADataSource xaDataSource;

  @Bean
  @Qualifier("brainslug")
  public LocalContainerEntityManagerFactoryBean entityManager() throws Throwable  {
    LocalContainerEntityManagerFactoryBean entityManager = createFactoryBean();

    entityManager.setJtaDataSource(dataSource());

    if (useJta()) {
      initJtaPlatform(jtaTransactionManager());
    }

    entityManager.setJpaProperties(jpaProperties());
    entityManager.setPackagesToScan("brainslug.jpa.entity");

    entityManager.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    entityManager.setPersistenceProviderClass(HibernatePersistenceProvider.class);

    return entityManager;
  }

  protected LocalContainerEntityManagerFactoryBean createFactoryBean() {
    return new LocalContainerEntityManagerFactoryBean();
  }

  protected void initJtaPlatform(JtaTransactionManager platformTransactionManager) {
    SpringHibernateJtaPlatform.setJtaTransactionManager(platformTransactionManager);
  }

  protected boolean useJta() {
    return true;
  }

  protected Properties jpaProperties() {
    Properties properties = new Properties();
    if (useJta()) {
      properties.setProperty("hibernate.transaction.jta.platform", SpringHibernateJtaPlatform.class.getName());
    }
    return properties;
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
  JPQLTemplates jpqlTemplates() {
    return new HQLTemplates();
  }

  @Bean
  public UserTransaction userTransaction() throws Throwable {
    UserTransactionImp userTransactionImp = new UserTransactionImp();
    userTransactionImp.setTransactionTimeout(8000);
    return userTransactionImp;
  }

  @Bean(destroyMethod = "close", initMethod = "init")
  public TransactionManager transactionManager() throws Throwable {
    UserTransactionManager userTransactionManager = new UserTransactionManager();
    userTransactionManager.setForceShutdown(false);

    return userTransactionManager;
  }

  @Bean
  public JtaTransactionManager jtaTransactionManager()  throws Throwable {
    return new JtaTransactionManager(userTransaction(), transactionManager());
  }
}