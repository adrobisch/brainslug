package brainslug.jdbc;

import brainslug.flow.Identifier;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.execution.async.AsyncTriggerQuery;
import brainslug.flow.execution.async.AsyncTriggerStore;
import brainslug.jdbc.entity.AsyncTaskEntity;
import brainslug.jdbc.entity.query.QAsyncTaskEntity;
import brainslug.util.IdGenerator;
import brainslug.util.IdUtil;
import brainslug.util.Option;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.ConstructorExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class JpaAsyncTriggerStore implements AsyncTriggerStore {

  private Logger log = LoggerFactory.getLogger(JpaAsyncTriggerStore.class);

  protected final Database database;
  protected final IdGenerator idGenerator;

  public JpaAsyncTriggerStore(Database database, IdGenerator idGenerator) {
    this.database = database;
    this.idGenerator = idGenerator;
  }

  @Override
  public AsyncTrigger storeTrigger(AsyncTrigger asyncTrigger) {
    Option<AsyncTrigger> existingTask = getTrigger(asyncTrigger.getNodeId(),
      asyncTrigger.getInstanceId(),
      asyncTrigger.getDefinitionId()
    );

    if (existingTask.isPresent()) {
      return updatedTask(existingTask.get(), asyncTrigger);
    } else {
      return insertTask(asyncTrigger, generateId(), getCreatedDate());
    }
  }

  protected long getCreatedDate() {
    return new Date().getTime();
  }

  protected Identifier generateId() {
    return idGenerator.generateId();
  }

  protected AsyncTrigger insertTask(AsyncTrigger asyncTrigger, Identifier asyncTaskId, long createdDate) {
    log.debug("inserting async task: {}", asyncTrigger);

    AsyncTaskEntity taskEntity = new AsyncTaskEntity()
      .withCreated(createdDate)
      .withId(asyncTaskId.stringValue())
      .withDueDate(asyncTrigger.getDueDate())
      .withDefinitionId(asyncTrigger.getDefinitionId().stringValue())
      .withInstanceId(asyncTrigger.getInstanceId().stringValue())
      .withMaxRetries(asyncTrigger.getMaxRetries())
      .withRetries(asyncTrigger.getRetries())
      .withTaskNodeId(asyncTrigger.getNodeId().stringValue());

    database.insertOrUpdate(
      taskEntity
    );

    return asyncTrigger
      .withId(IdUtil.id(taskEntity.getId()))
      .withVersion(taskEntity.getVersion())
      .withCreatedDate(createdDate);
  }

  protected AsyncTrigger updatedTask(AsyncTrigger existingTask, AsyncTrigger updatedTask) {
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
  public boolean removeTrigger(AsyncTrigger asyncTrigger) {
    log.debug("removing async task: {}", asyncTrigger);

    Long deletedCount = database.delete(QAsyncTaskEntity.asyncTaskEntity)
      .where(QAsyncTaskEntity.asyncTaskEntity.id.eq(asyncTrigger.getId().get().stringValue()))
      .execute();
    return deletedCount > 0;
  }

  @Override
  public List<AsyncTrigger> getTriggers(AsyncTriggerQuery taskQuery) {
    JPAQuery query = database.query()
      .from(QAsyncTaskEntity.asyncTaskEntity)
      .limit(taskQuery.getMaxCount());

    return queryWithOptionalDueDate(query, taskQuery).list(asyncTaskConstructor());
  }

  JPAQuery queryWithOptionalDueDate(JPAQuery jpaQuery, AsyncTriggerQuery taskQuery) {
    if (taskQuery.getOverdueDate().isPresent()) {
      long dueDate = taskQuery.getOverdueDate().get().getTime();

      return jpaQuery
        .where(QAsyncTaskEntity.asyncTaskEntity.dueDate.loe(dueDate));
    }
    return jpaQuery;
  }

  protected ConstructorExpression<AsyncTrigger> asyncTaskConstructor() {
    return ConstructorExpression.create(AsyncTrigger.class,
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
  public Option<AsyncTrigger> getTrigger(Identifier taskNodeId, Identifier instanceId, Identifier definitionId) {
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
