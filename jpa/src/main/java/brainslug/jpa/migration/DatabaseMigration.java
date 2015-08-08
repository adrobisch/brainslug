package brainslug.jpa.migration;

import liquibase.Liquibase;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DatabaseMigration {

  Liquibase liquibase;

  public DatabaseMigration(Liquibase liquibase) {
    this.liquibase = liquibase;
  }

  @PostConstruct
  public void migrate() {
    try {
      liquibase.update("");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
