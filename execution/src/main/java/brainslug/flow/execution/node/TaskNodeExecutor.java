package brainslug.flow.execution.node;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.definition.DefinitionStore;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.execution.async.AsyncTriggerErrorDetails;
import brainslug.flow.execution.async.AsyncTriggerScheduler;
import brainslug.flow.execution.expression.ExpressionEvaluator;
import brainslug.flow.execution.node.task.CallDefinitionExecutor;
import brainslug.flow.execution.node.task.ScriptExecutor;
import brainslug.flow.expression.PredicateExpression;
import brainslug.flow.node.task.AbstractTaskDefinition;
import brainslug.flow.node.task.GoalDefinition;
import brainslug.flow.node.task.HandlerCallDefinition;
import brainslug.util.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskNodeExecutor extends DefaultNodeExecutor<AbstractTaskDefinition<?>> {

  private Logger log = LoggerFactory.getLogger(TaskNodeExecutor.class);

  DefinitionStore definitionStore;
  ExpressionEvaluator expressionEvaluator;
  CallDefinitionExecutor callDefinitionExecutor;
  AsyncTriggerScheduler asyncTriggerScheduler;
  ScriptExecutor scriptExecutor;

  public TaskNodeExecutor(DefinitionStore definitionStore,
                          ExpressionEvaluator expressionEvaluator,
                          CallDefinitionExecutor callDefinitionExecutor,
                          AsyncTriggerScheduler asyncTriggerScheduler,
                          ScriptExecutor scriptExecutor) {
    this.definitionStore = definitionStore;
    this.expressionEvaluator = expressionEvaluator;
    this.callDefinitionExecutor = callDefinitionExecutor;
    this.asyncTriggerScheduler = asyncTriggerScheduler;
    this.scriptExecutor = scriptExecutor;
  }

  @Override
  public FlowNodeExecutionResult execute(AbstractTaskDefinition<?> taskDefinition, ExecutionContext execution) {
    if (taskDefinition.getGoal().isPresent() && goalIsFulfilled(taskDefinition.getGoal().get(), execution)) {
      return takeAllAndRemoveFirst(taskDefinition, execution.getInstance());
    } else if (taskDefinition.isAsync() && !execution.isAsync()) {
      scheduleAsyncTask(taskDefinition, execution);
      return takeNone(taskDefinition, execution.getInstance());
    } else if (isExecutable(taskDefinition)) {
      return executeWithOptionalAsyncRetry(taskDefinition, execution);
    } else {
      log.warn("executing task node without execution definition, " +
        "please specify the task node execution by using a delegate class or call definition to actually do something in this task");
      return takeAllAndRemoveFirst(taskDefinition, execution.getInstance());
    }
  }

  protected FlowNodeExecutionResult executeWithOptionalAsyncRetry(AbstractTaskDefinition<?> taskDefinition, ExecutionContext execution) {
    try {
      if (Option.of(taskDefinition.getDelegateClass()).isPresent()) {
        HandlerCallDefinition callDefinition = new HandlerCallDefinition(execution.service(taskDefinition.getDelegateClass()));
        callDefinitionExecutor.execute(callDefinition, execution);
        return takeAllAndRemoveFirst(taskDefinition, execution.getInstance());
      } else if (Option.of(taskDefinition.getMethodCall()).isPresent()) {
        callDefinitionExecutor.execute(taskDefinition.getMethodCall(), execution);
        return takeAllAndRemoveFirst(taskDefinition, execution.getInstance());
      } else if(taskDefinition.getTaskScript().isPresent()) {
        scriptExecutor.execute(taskDefinition.getTaskScript().get(), execution);
        return takeAllAndRemoveFirst(taskDefinition, execution.getInstance());
      }
      throw new IllegalStateException("this method should only be called with executable " + taskDefinition);
    } catch (Exception executionException) {
      log.error(String.format("error during task (%s) execution: ", taskDefinition), executionException);
      if (taskDefinition.isRetryAsync()) {
        return scheduleRetry(executionException, taskDefinition, execution);
      }
      return takeNone(taskDefinition, execution.getInstance())
          .failed(true)
          .setException(executionException);
    }
  }

  private FlowNodeExecutionResult scheduleRetry(Exception e, AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    asyncTriggerScheduler
      .schedule(
        new AsyncTrigger()
          .incrementRetries()
          .withErrorDetails(new AsyncTriggerErrorDetails(e))
          .withNodeId(taskDefinition.getId())
          .withInstanceId(execution.getInstance().getIdentifier())
          .withDefinitionId(execution.getInstance().getDefinitionId())
      );
    return new FlowNodeExecutionResult(taskDefinition)
        .setException(e)
        .failed(true);
  }

  protected boolean isExecutable(AbstractTaskDefinition taskDefinition) {
    return taskDefinition.getDelegateClass() != null ||
            taskDefinition.getMethodCall() != null ||
            taskDefinition.getTaskScript().isPresent();
  }

  protected boolean goalIsFulfilled(GoalDefinition goal, ExecutionContext execution) {
    PredicateExpression goalPredicate = goal.getPredicate();

    return goalPredicate != null && expressionEvaluator.evaluate(goalPredicate, execution, Boolean.class);
  }

  protected void scheduleAsyncTask(AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    asyncTriggerScheduler
      .schedule(
        new AsyncTrigger()
          .withNodeId(taskDefinition.getId())
          .withInstanceId(execution.getInstance().getIdentifier())
          .withDefinitionId(execution.getInstance().getDefinitionId())
      );
  }
}
