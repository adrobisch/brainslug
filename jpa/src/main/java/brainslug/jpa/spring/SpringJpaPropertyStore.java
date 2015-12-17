package brainslug.jpa.spring;

import brainslug.jpa.Database;
import brainslug.jpa.JpaInstanceStore;
import brainslug.jpa.JpaPropertyStore;
import brainslug.jpa.util.ObjectSerializer;
import brainslug.util.IdGenerator;
import brainslug.util.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringJpaPropertyStore extends JpaPropertyStore {
  @Autowired(required = false)
  ObjectSerializer objectSerializer;

  @Autowired
  public SpringJpaPropertyStore(Database database, IdGenerator idGenerator, JpaInstanceStore jpaInstanceStore) {
    super(database, idGenerator, jpaInstanceStore);
    if (objectSerializer != null) {
      withSerializer(objectSerializer);
    }
  }
}
