package brainslug.jdbc;

import brainslug.flow.Identifier;
import brainslug.flow.execution.async.AsyncTask;
import brainslug.flow.execution.async.AsyncTaskQuery;
import brainslug.util.IdUtil;
import brainslug.util.Option;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class JpaAsyncTaskStoreTest extends AbstractDatabaseTest {

  @Test
  public void shouldStoreTask() throws Exception {
    when(idGeneratorMock.generateId()).thenReturn(IdUtil.id("newTaskId"));

    JpaAsyncTaskStore asyncTaskStore = createJdbcAsyncTaskStore();

    AsyncTask storedTask = storeTask(asyncTaskStore);

    Assertions.assertThat(storedTask.getVersion()).isEqualTo(0);
    Assertions.assertThat(storedTask.getId().get().stringValue()).isEqualTo("newTaskId");
    Assertions.assertThat(storedTask.getCreatedDate()).isNotEqualTo(0);
  }

  @Test
  public void shouldUpdateTask() throws Exception {
    when(idGeneratorMock.generateId()).thenReturn(IdUtil.id("newTaskId"));

    JpaAsyncTaskStore asyncTaskStore = createJdbcAsyncTaskStore();

    AsyncTask storedTask = storeTask(asyncTaskStore);

    storedTask.incrementRetries();

    AsyncTask updatedTask = asyncTaskStore.storeTask(storedTask);

    Assertions.assertThat(updatedTask.getRetries()).isEqualTo(1);
    Assertions.assertThat(updatedTask.getVersion()).isEqualTo(1);
  }

  private AsyncTask storeTask(JpaAsyncTaskStore asyncTaskStore) {
    return asyncTaskStore.storeTask(new AsyncTask()
          .withTaskNodeId(IdUtil.id("taskNodeId"))
          .withDefinitionId(IdUtil.id("definitionId"))
          .withDueDate(new Date(0).getTime())
          .withInstanceId(IdUtil.id("instanceId"))
      );
  }

  @Test
  public void shouldListTasks() throws Exception {
    when(idGeneratorMock.generateId()).thenAnswer(new Answer<Identifier>() {
      @Override
      public Identifier answer(InvocationOnMock invocation) throws Throwable {
        return IdUtil.id(UUID.randomUUID().toString());
      }
    });

    JpaAsyncTaskStore asyncTaskStore = createJdbcAsyncTaskStore();

    for (int i = 0; i < 5; i++) {
      asyncTaskStore.storeTask(new AsyncTask()
          .withTaskNodeId(IdUtil.id("taskNodeId"))
          .withDefinitionId(IdUtil.id("definitionId"))
          .withDueDate(new Date(0).getTime())
          .withInstanceId(IdUtil.id("instanceId" + i))
      );
    }

    Assertions.assertThat(asyncTaskStore.getTasks(new AsyncTaskQuery().withMaxCount(3)))
      .hasSize(3);
  }

  @Test
  public void shouldGetSingleTask() throws Exception {
    when(idGeneratorMock.generateId()).thenReturn(IdUtil.id("newAsyncTaskId"));
    JpaAsyncTaskStore asyncTaskStore = createJdbcAsyncTaskStore();

    AsyncTask task = storeTask(asyncTaskStore);

    Option<AsyncTask> taskFromStore = asyncTaskStore.getTask(task.getTaskNodeId(), task.getInstanceId(), task.getDefinitionId());
    Assertions.assertThat(taskFromStore.isPresent()).isTrue();

    AsyncTask asyncTask = taskFromStore.get();
    Assertions.assertThat(asyncTask.getInstanceId().stringValue()).isEqualTo("instanceId");
    Assertions.assertThat(asyncTask.getTaskNodeId().stringValue()).isEqualTo("taskNodeId");
    Assertions.assertThat(asyncTask.getDefinitionId().stringValue()).isEqualTo("definitionId");
  }

  @Test
  public void shouldRemoveTask() throws Exception {
    when(idGeneratorMock.generateId()).thenReturn(IdUtil.id("newAsyncTaskId"));
    JpaAsyncTaskStore asyncTaskStore = createJdbcAsyncTaskStore();

    AsyncTask task = storeTask(asyncTaskStore);
    boolean deleted = asyncTaskStore.removeTask(task);

    Assertions.assertThat(deleted).isTrue();
    Assertions.assertThat(asyncTaskStore.getTasks(new AsyncTaskQuery().withMaxCount(50))).hasSize(0);
  }

  JpaAsyncTaskStore createJdbcAsyncTaskStore() {
    return new JpaAsyncTaskStore(database, idGeneratorMock);
  }

}