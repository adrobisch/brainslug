package brainslug.flow.execution.token;

import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.CallDefinitionExecutor;
import brainslug.flow.execution.DefinitionStore;
import brainslug.flow.execution.FlowNodeExecutionResult;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.execution.async.AsyncTriggerErrorDetails;
import brainslug.flow.execution.async.AsyncTriggerScheduler;
import brainslug.flow.execution.expression.PredicateEvaluator;
import brainslug.flow.expression.PredicateDefinition;
import brainslug.flow.node.task.AbstractTaskDefinition;
import brainslug.flow.node.task.HandlerCallDefinition;
import brainslug.util.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskNodeExecutor extends DefaultNodeExecutor<TaskNodeExecutor, AbstractTaskDefinition> {

  private Logger log = LoggerFactory.getLogger(TaskNodeExecutor.class);

  DefinitionStore definitionStore;
  PredicateEvaluator predicateEvaluator;
  CallDefinitionExecutor callDefinitionExecutor;
  AsyncTriggerScheduler asyncTriggerScheduler;

  public TaskNodeExecutor(DefinitionStore definitionStore, PredicateEvaluator predicateEvaluator, CallDefinitionExecutor callDefinitionExecutor, AsyncTriggerScheduler asyncTriggerScheduler) {
    this.definitionStore = definitionStore;
    this.predicateEvaluator = predicateEvaluator;
    this.callDefinitionExecutor = callDefinitionExecutor;
    this.asyncTriggerScheduler = asyncTriggerScheduler;
  }

  @Override
  public brainslug.flow.execution.FlowNodeExecutionResult execute(AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    removeIncomingTokens(execution.getTrigger());

    if (taskDefinition.getGoal().isPresent() && goalIsFulfilled((Identifier) taskDefinition.getGoal().get(), execution)) {
      return takeAll(taskDefinition);
    } else if (taskDefinition.isAsync() && !execution.getTrigger().isAsync()) {
      scheduleAsyncTask(taskDefinition, execution);
      return takeNone();
    } else if (isExecutable(taskDefinition)) {
      return executeWithOptionalAsyncRetry(taskDefinition, execution);
    } else {
      log.warn("executing task node without execution definition, " +
        "please specify the task node execution by using a delegate class or call definition to actually do something in this task");
      return takeAll(taskDefinition);
    }
  }

  protected FlowNodeExecutionResult executeWithOptionalAsyncRetry(AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    try {
      if (Option.of(taskDefinition.getDelegateClass()).isPresent()) {
        HandlerCallDefinition callDefinition = new HandlerCallDefinition(execution.service(taskDefinition.getDelegateClass()));
        callDefinitionExecutor.execute(callDefinition, execution);
        return takeAll(taskDefinition);
      } else if (Option.of(taskDefinition.getMethodCall()).isPresent()) {
        callDefinitionExecutor.execute(taskDefinition.getMethodCall(), execution);
        return takeAll(taskDefinition);
      }
      throw new IllegalStateException("this method should only be called with executable " + taskDefinition);
    } catch (Exception e) {
      log.error(String.format("error during task (%s) execution: ", taskDefinition), e);
      if (taskDefinition.isRetryAsync()) {
        return scheduleRetry(e, taskDefinition, execution);
      }
      return takeNone();
    }
  }

  private FlowNodeExecutionResult scheduleRetry(Exception e, AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    asyncTriggerScheduler
      .schedule(
        new AsyncTrigger()
          .incrementRetries()
          .withErrorDetails(new AsyncTriggerErrorDetails(e))
          .withNodeId(taskDefinition.getId())
          .withInstanceId(execution.getTrigger().getInstanceId())
          .withDefinitionId(execution.getTrigger().getDefinitionId())
      );
    return new FlowNodeExecutionResult().failed(true);
  }

  protected boolean isExecutable(AbstractTaskDefinition taskDefinition) {
    return taskDefinition.getDelegateClass() != null || taskDefinition.getMethodCall() != null;
  }

  protected boolean goalIsFulfilled(Identifier goalId, ExecutionContext execution) {
    FlowDefinition definition = definitionStore.findById(execution.getTrigger().getDefinitionId());

    Option<PredicateDefinition> goalPredicate = definition.getGoalPredicate(goalId);

    if (!goalPredicate.isPresent()) {
      return false;
    } else {
      return predicateEvaluator.evaluate(goalPredicate.get(), execution);
    }
  }

  protected void scheduleAsyncTask(AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    asyncTriggerScheduler
      .schedule(
        new AsyncTrigger()
          .withNodeId(taskDefinition.getId())
          .withInstanceId(execution.getTrigger().getInstanceId())
          .withDefinitionId(execution.getTrigger().getDefinitionId())
      );
  }
}
