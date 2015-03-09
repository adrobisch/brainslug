package brainslug.jpa.migration;

import liquibase.Liquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DatabaseMigration {

  @Autowired
  Liquibase liquibase;

  @PostConstruct
  public void migrate() {
    try {
      liquibase.update("");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
