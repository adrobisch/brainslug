package brainslug.jdbc;

import brainslug.flow.context.IdGenerator;
import brainslug.flow.execution.Token;
import brainslug.flow.execution.TokenList;
import brainslug.flow.model.Identifier;
import brainslug.util.Option;
import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.H2Templates;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.List;
import java.util.Random;

import static brainslug.util.IdUtil.id;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JdbcTokenStoreTest {

  IdGenerator idGeneratorMock = mock(IdGenerator.class);

  Identifier instanceId = id("instance");
  Identifier nodeId = id("node");
  Identifier tokenId = id("token");
  Identifier flowId = id("flow");

  @Test
  public void shouldInsertInstanceAndToken() throws Exception {
    // given:
    JdbcTokenStore jdbcTokenStore = createJdbcTokenStore();

    // when:
    Identifier instanceId = createInstanceWithSingleRootToken(jdbcTokenStore);

    assertThat(instanceId).isNotNull();

    // then:
    TokenList instanceTokens = jdbcTokenStore.getInstanceTokens(instanceId);
    assertThat(instanceTokens.getTokens())
      .contains(new Token(tokenId, nodeId, Option.<Identifier>empty(), Option.of(instanceId), false))
      .hasSize(1);
  }

  @Test
  public void shouldDeleteToken() throws Exception {
    // given:
    JdbcTokenStore jdbcTokenStore = createJdbcTokenStore();
    Identifier instanceId = createInstanceWithSingleRootToken(jdbcTokenStore);
    // when:
    jdbcTokenStore.removeToken(instanceId, tokenId);

    assertThat(jdbcTokenStore.getNodeTokens(nodeId, instanceId).getTokens())
      .contains(new Token(tokenId, nodeId, Option.<Identifier>empty(), Option.of(instanceId), true))
      .hasSize(1);
  }

  private Identifier createInstanceWithSingleRootToken(JdbcTokenStore jdbcTokenStore) {
    when(idGeneratorMock.generateId()).thenReturn(instanceId);
    Identifier instanceId = jdbcTokenStore.createInstance(flowId);

    when(idGeneratorMock.generateId()).thenReturn(tokenId);
    jdbcTokenStore.addToken(instanceId, nodeId, Option.<Identifier>empty());
    return instanceId;
  }

  JdbcTokenStore createJdbcTokenStore() {
    return new JdbcTokenStore(createDataBase(), idGeneratorMock);
  }

  Database createDataBase() {
    return new Database(withMigrations(datasource()), new Configuration(new H2Templates()));
  }

  private DataSource withMigrations(DataSource datasource) {
    Flyway flyway = new Flyway();
    flyway.setDataSource(datasource);
    flyway.migrate();
    return datasource;
  }

  DataSource datasource() {
    HikariConfig config = new HikariConfig();
    config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
    config.addDataSourceProperty("url", "jdbc:h2:mem:testdb" + new Random().nextInt());
    config.addDataSourceProperty("user", "sa");
    config.addDataSourceProperty("password", "");

    return new HikariDataSource(config);
  }
}