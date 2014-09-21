package brainslug.jdbc;

import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;

import javax.sql.DataSource;
import java.sql.SQLException;

public class Database {
  private final DataSource dataSource;
  private final Configuration configuration;
  private final SQLTemplates dialect;

  public Database(DataSource dataSource, Configuration configuration) {
    this.dataSource = dataSource;
    this.configuration = configuration;
    this.dialect = configuration.getTemplates();
  }

  public SQLQuery query() {
    try {
      return new SQLQuery(dataSource.getConnection(), dialect);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public SQLInsertClause insert(RelationalPath<?> path) {
    try {
      return new SQLInsertClause(dataSource.getConnection(), dialect, path);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public SQLUpdateClause update(RelationalPath<?> path) {
    try {
      return new SQLUpdateClause(dataSource.getConnection(), dialect, path);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public SQLDeleteClause delete(RelationalPath<?> path) {
    try {
      return new SQLDeleteClause(dataSource.getConnection(), dialect, path);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
