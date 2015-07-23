package brainslug.jpa.spring;

import brainslug.jpa.Database;
import brainslug.jpa.JpaInstanceStore;
import brainslug.jpa.JpaPropertyStore;
import brainslug.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringJpaPropertyStore extends JpaPropertyStore {
  @Autowired
  public SpringJpaPropertyStore(Database database, IdGenerator idGenerator, JpaInstanceStore jpaInstanceStore) {
    super(database, idGenerator, jpaInstanceStore);
  }
}
