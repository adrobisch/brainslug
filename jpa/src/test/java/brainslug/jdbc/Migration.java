package brainslug.jdbc;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Migration {

  @Autowired
  Flyway flyway;

  public void migrate() {
    flyway.migrate();
  }

  public void clean() {
    flyway.clean();
  }

}
