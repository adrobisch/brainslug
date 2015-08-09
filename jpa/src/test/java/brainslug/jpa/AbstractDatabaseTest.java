package brainslug.jpa;

import brainslug.jpa.migration.DatabaseMigration;
import brainslug.jpa.spring.SpringDatabaseConfiguration;
import brainslug.jpa.spring.SpringDatabaseMigrationConfiguration;
import brainslug.jpa.spring.SpringHibernateConfiguration;
import brainslug.spring.SpringBrainslugConfiguration;
import brainslug.util.IdGenerator;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
  TestDataSourceConfiguration.class,
  SpringHibernateConfiguration.class,
  SpringDatabaseConfiguration.class,
  SpringDatabaseMigrationConfiguration.class,
  SpringBrainslugConfiguration.class
})
@Transactional
public abstract class AbstractDatabaseTest {

  @Autowired
  Database database;

  @Autowired
  DatabaseMigration databaseMigration;

  IdGenerator idGeneratorMock = mock(IdGenerator.class);
}
