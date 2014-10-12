package brainslug.jdbc;

import com.mysema.query.jpa.JPQLTemplates;
import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;

import javax.persistence.EntityManager;

public class Database {

  private final EntityManager entityManager;
  private final JPQLTemplates jpqlDialect;

  public Database(EntityManager entityManager, JPQLTemplates jpqlTemplates) {
    this.entityManager = entityManager;
    this.jpqlDialect = jpqlTemplates;
  }

  public JPAQuery query() {
    return new JPAQuery(entityManager, jpqlDialect);
  }

  public <T> T insertOrUpdate(T entity) {
    entityManager.persist(entity);
    return entity;
  }

  public void flush() {
    entityManager.flush();
  }

  public JPADeleteClause delete(EntityPath<?> path) {
    return new JPADeleteClause(entityManager, path, jpqlDialect);
  }
}
