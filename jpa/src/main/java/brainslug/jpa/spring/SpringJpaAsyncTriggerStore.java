package brainslug.jpa.spring;

import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.jpa.Database;
import brainslug.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringJpaAsyncTriggerStore extends brainslug.jpa.JpaAsyncTriggerStore {
  @Autowired
  public SpringJpaAsyncTriggerStore(Database database, IdGenerator idGenerator) {
    super(database, idGenerator);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public AsyncTrigger updateTrigger(AsyncTrigger asyncTrigger) {
    return super.updateTrigger(asyncTrigger);
  }
}
