package brainslug.jpa;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.execution.async.AsyncTriggerErrorDetails;
import brainslug.flow.execution.async.AsyncTriggerQuery;
import brainslug.jpa.entity.AsyncTaskEntity;
import brainslug.util.IdUtil;
import brainslug.util.Option;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class JpaAsyncTriggerStoreTest extends AbstractDatabaseTest {

  @Test
  public void shouldStoreTask() throws Exception {
    when(idGeneratorMock.generateId()).thenReturn(IdUtil.id("newTaskId"));

    JpaAsyncTriggerStore asyncTaskStore = createJdbcAsyncTaskStore();

    AsyncTrigger storedTask = storeTask(asyncTaskStore);

    Assertions.assertThat(storedTask.getVersion()).isEqualTo(0);
    Assertions.assertThat(storedTask.getId().get().stringValue()).isEqualTo("newTaskId");
    Assertions.assertThat(storedTask.getCreatedDate()).isNotEqualTo(0);
  }

  @Test
  public void shouldUpdateTaskWithErrorDetails() throws Exception {
    when(idGeneratorMock.generateId()).thenReturn(IdUtil.id("newTaskId"));

    JpaAsyncTriggerStore asyncTaskStore = createJdbcAsyncTaskStore();

    AsyncTrigger storedTask = storeTask(asyncTaskStore);

    storedTask.withErrorDetails(new AsyncTriggerErrorDetails(new RuntimeException("a error")));
    asyncTaskStore.storeTrigger(storedTask);

    AsyncTaskEntity entity = asyncTaskStore.getTaskEntity(storedTask.getId().get());

    Assertions.assertThat(entity.getErrorDetails()).isNotNull();
    Assertions.assertThat(entity.getErrorDetails().getMessage()).isEqualTo("a error");
    Assertions.assertThat(entity.getErrorDetails().getExceptionType()).isEqualTo("java.lang.RuntimeException");
    Assertions.assertThat(new String(entity.getErrorDetails().getStackTrace())).isNotEmpty();
  }

  @Test
  public void shouldReturnOverdueTasks() throws Exception {
    when(idGeneratorMock.generateId()).thenReturn(IdUtil.id("newTaskId"));

    JpaAsyncTriggerStore asyncTaskStore = createJdbcAsyncTaskStore();

    storeTask(asyncTaskStore);

    Assertions.assertThat(asyncTaskStore.getTriggers(new AsyncTriggerQuery().withOverdueDate(new Date(101)))).hasSize(1);
    Assertions.assertThat(asyncTaskStore.getTriggers(new AsyncTriggerQuery().withOverdueDate(new Date(99)))).hasSize(0);
  }

  @Test
  public void shouldUpdateTask() throws Exception {
    when(idGeneratorMock.generateId()).thenReturn(IdUtil.id("newTaskId"));

    JpaAsyncTriggerStore asyncTaskStore = createJdbcAsyncTaskStore();

    AsyncTrigger storedTask = storeTask(asyncTaskStore);

    storedTask.incrementRetries();

    AsyncTrigger updatedTask = asyncTaskStore.storeTrigger(storedTask);

    Assertions.assertThat(updatedTask.getRetries()).isEqualTo(1);
    Assertions.assertThat(updatedTask.getVersion()).isEqualTo(1);
  }

  private AsyncTrigger storeTask(JpaAsyncTriggerStore asyncTaskStore) {
    return asyncTaskStore.storeTrigger(testTrigger());
  }

  private AsyncTrigger testTrigger() {
    return new AsyncTrigger()
        .withNodeId(IdUtil.id("taskNodeId"))
        .withDefinitionId(IdUtil.id("definitionId"))
        .withDueDate(new Date(100).getTime())
        .withInstanceId(IdUtil.id("instanceId"));
  }

  @Test
  public void shouldListTasks() throws Exception {
    when(idGeneratorMock.generateId()).thenAnswer(new Answer<Identifier>() {
      @Override
      public Identifier answer(InvocationOnMock invocation) throws Throwable {
        return IdUtil.id(UUID.randomUUID().toString());
      }
    });

    JpaAsyncTriggerStore asyncTaskStore = createJdbcAsyncTaskStore();

    for (int i = 0; i < 5; i++) {
      asyncTaskStore.storeTrigger(new AsyncTrigger()
          .withNodeId(IdUtil.id("taskNodeId"))
          .withDefinitionId(IdUtil.id("definitionId"))
          .withDueDate(new Date(0).getTime())
          .withInstanceId(IdUtil.id("instanceId" + i))
      );
    }

    Assertions.assertThat(asyncTaskStore.getTriggers(new AsyncTriggerQuery().withMaxCount(3)))
      .hasSize(3);
  }

  @Test
  public void shouldGetSingleTask() throws Exception {
    when(idGeneratorMock.generateId()).thenReturn(IdUtil.id("newAsyncTaskId"));
    JpaAsyncTriggerStore asyncTaskStore = createJdbcAsyncTaskStore();

    AsyncTrigger task = storeTask(asyncTaskStore);

    Option<AsyncTrigger> taskFromStore = asyncTaskStore.getTrigger(task.getNodeId(), task.getInstanceId(), task.getDefinitionId());
    Assertions.assertThat(taskFromStore.isPresent()).isTrue();

    AsyncTrigger asyncTrigger = taskFromStore.get();
    Assertions.assertThat(asyncTrigger.getInstanceId().stringValue()).isEqualTo("instanceId");
    Assertions.assertThat(asyncTrigger.getNodeId().stringValue()).isEqualTo("taskNodeId");
    Assertions.assertThat(asyncTrigger.getDefinitionId().stringValue()).isEqualTo("definitionId");
  }

  @Test
  public void shouldRemoveTask() throws Exception {
    when(idGeneratorMock.generateId()).thenReturn(IdUtil.id("newAsyncTaskId"));
    JpaAsyncTriggerStore asyncTaskStore = createJdbcAsyncTaskStore();

    AsyncTrigger task = storeTask(asyncTaskStore);
    boolean deleted = asyncTaskStore.removeTrigger(task);

    Assertions.assertThat(deleted).isTrue();
    Assertions.assertThat(asyncTaskStore.getTriggers(new AsyncTriggerQuery().withMaxCount(50))).hasSize(0);
  }

  JpaAsyncTriggerStore createJdbcAsyncTaskStore() {
    return new JpaAsyncTriggerStore(database, idGeneratorMock);
  }

}