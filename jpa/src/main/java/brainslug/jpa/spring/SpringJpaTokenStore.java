package brainslug.jpa.spring;

import brainslug.jpa.Database;
import brainslug.jpa.JpaTokenStore;
import brainslug.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringJpaTokenStore extends JpaTokenStore {
  @Autowired
  public SpringJpaTokenStore(Database database, IdGenerator idGenerator) {
    super(database, idGenerator);
  }
}
