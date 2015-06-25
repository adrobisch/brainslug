package brainslug.jpa.spring;

import brainslug.jpa.Database;
import brainslug.jpa.JpaInstanceStore;
import brainslug.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringJpaInstanceStore extends JpaInstanceStore {
  @Autowired
  public SpringJpaInstanceStore(Database database, IdGenerator idGenerator) {
    super(database, idGenerator);
  }
}
