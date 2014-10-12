package brainslug.jdbc;

import brainslug.util.IdGenerator;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringDatabaseConfiguration.class})
@Transactional
public abstract class AbstractDatabaseTest {

  @Autowired
  Migration migration;

  @Autowired
  Database database;

  @Before
  public void setUp() {
    migration.clean();
    migration.migrate();
  }

  IdGenerator idGeneratorMock = mock(IdGenerator.class);
}
