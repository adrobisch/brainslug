package brainslug.jdbc;

import brainslug.flow.Identifier;
import brainslug.flow.execution.async.AsyncTask;
import brainslug.flow.execution.async.AsyncTaskQuery;
import brainslug.flow.execution.async.AsyncTaskStore;
import brainslug.jdbc.entity.AsyncTaskEntity;
import brainslug.jdbc.entity.query.QAsyncTaskEntity;
import brainslug.util.IdGenerator;
import brainslug.util.IdUtil;
import brainslug.util.Option;
import com.mysema.query.types.ConstructorExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class JpaAsyncTaskStore implements AsyncTaskStore {

  private Logger log = LoggerFactory.getLogger(JpaAsyncTaskStore.class);

  protected final Database database;
  protected final IdGenerator idGenerator;

  public JpaAsyncTaskStore(Database database, IdGenerator idGenerator) {
    this.database = database;
    this.idGenerator = idGenerator;
  }

  @Override
  public AsyncTask storeTask(AsyncTask asyncTask) {
    Option<AsyncTask> existingTask = getTask(asyncTask.getTaskNodeId(),
      asyncTask.getInstanceId(),
      asyncTask.getDefinitionId()
    );

    if (existingTask.isPresent()) {
      return updatedTask(existingTask.get(), asyncTask);
    } else {
      return insertTask(asyncTask, generateId(), getCreatedDate());
    }
  }

  protected long getCreatedDate() {
    return new Date().getTime();
  }

  protected Identifier generateId() {
    return idGenerator.generateId();
  }

  protected AsyncTask insertTask(AsyncTask asyncTask, Identifier asyncTaskId, long createdDate) {
    log.debug("inserting async task: {}", asyncTask);

    AsyncTaskEntity taskEntity = new AsyncTaskEntity()
      .withCreated(createdDate)
      .withId(asyncTaskId.stringValue())
      .withDueDate(asyncTask.getDueDate())
      .withDefinitionId(asyncTask.getDefinitionId().stringValue())
      .withInstanceId(asyncTask.getInstanceId().stringValue())
      .withMaxRetries(asyncTask.getMaxRetries())
      .withRetries(asyncTask.getRetries())
      .withTaskNodeId(asyncTask.getTaskNodeId().stringValue());

    database.insertOrUpdate(
      taskEntity
    );

    return asyncTask
      .withId(IdUtil.id(taskEntity.getId()))
      .withVersion(taskEntity.getVersion())
      .withCreatedDate(createdDate);
  }

  protected AsyncTask updatedTask(AsyncTask existingTask, AsyncTask updatedTask) {
    log.debug("updating async task: {}", existingTask);

    AsyncTaskEntity taskEntity = getTaskEntity(existingTask.getId().get());

    taskEntity
      .withRetries(updatedTask.getRetries())
      .withMaxRetries(updatedTask.getMaxRetries())
      .withDueDate(updatedTask.getDueDate());

    database.insertOrUpdate(taskEntity);
    database.flush();

    return updatedTask.withVersion(taskEntity.getVersion());
  }

  @Override
  public boolean removeTask(AsyncTask asyncTask) {
    log.debug("removing async task: {}", asyncTask);

    Long deletedCount = database.delete(QAsyncTaskEntity.asyncTaskEntity)
      .where(QAsyncTaskEntity.asyncTaskEntity.id.eq(asyncTask.getId().get().stringValue()))
      .execute();
    return deletedCount > 0;
  }

  @Override
  public List<AsyncTask> getTasks(AsyncTaskQuery taskQuery) {
    return database.query()
      .from(QAsyncTaskEntity.asyncTaskEntity)
      .limit(taskQuery.getMaxCount())
      .list(asyncTaskConstructor());
  }

  protected ConstructorExpression<AsyncTask> asyncTaskConstructor() {
    return ConstructorExpression.create(AsyncTask.class,
      QAsyncTaskEntity.asyncTaskEntity.id,
      QAsyncTaskEntity.asyncTaskEntity.taskNodeId,
      QAsyncTaskEntity.asyncTaskEntity.instanceId,
      QAsyncTaskEntity.asyncTaskEntity.definitionId,
      QAsyncTaskEntity.asyncTaskEntity.created,
      QAsyncTaskEntity.asyncTaskEntity.dueDate,
      QAsyncTaskEntity.asyncTaskEntity.retries,
      QAsyncTaskEntity.asyncTaskEntity.maxRetries,
      QAsyncTaskEntity.asyncTaskEntity.version
    );
  }

  @Override
  public Option<AsyncTask> getTask(Identifier taskNodeId, Identifier instanceId, Identifier definitionId) {
    return Option.of(database.query().from(QAsyncTaskEntity.asyncTaskEntity)
      .where(
        QAsyncTaskEntity.asyncTaskEntity.taskNodeId.eq(taskNodeId.stringValue()),
        QAsyncTaskEntity.asyncTaskEntity.instanceId.eq(instanceId.stringValue()),
        QAsyncTaskEntity.asyncTaskEntity.definitionId.eq(definitionId.stringValue())
      ).singleResult(asyncTaskConstructor()));
  }

  protected AsyncTaskEntity getTaskEntity(Identifier id) {
    return database.query().from(QAsyncTaskEntity.asyncTaskEntity)
      .where(
        QAsyncTaskEntity.asyncTaskEntity.id.eq(id.stringValue())
      ).singleResult(QAsyncTaskEntity.asyncTaskEntity);
  }

}
