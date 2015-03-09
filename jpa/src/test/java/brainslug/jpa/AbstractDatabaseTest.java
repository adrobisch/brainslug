package brainslug.jpa;

import brainslug.jpa.migration.DatabaseMigration;
import brainslug.jpa.spring.SpringDatabaseConfiguration;
import brainslug.jpa.spring.SpringDatabaseMigrationConfiguration;
import brainslug.util.IdGenerator;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
  TestDataSourceConfiguration.class,
  SpringDatabaseConfiguration.class,
  SpringDatabaseMigrationConfiguration.class
})
@Transactional
public abstract class AbstractDatabaseTest {

  @Autowired
  Database database;

  @Autowired
  DatabaseMigration databaseMigration;

  IdGenerator idGeneratorMock = mock(IdGenerator.class);

  @Before
  public void runMigrations() {
    afterMigration();
  }

  protected void afterMigration() {
  }
}
