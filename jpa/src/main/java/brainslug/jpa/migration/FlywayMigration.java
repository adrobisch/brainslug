package brainslug.jpa.migration;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

@Service
public class FlywayMigration {

  @Autowired
  Flyway flyway;

  public void clean() {
    flyway.clean();
  }

  public void migrate() {
    flyway.migrate();
  }

  @PostConstruct
  public void doMigration() {
    migrate();
  }

}
