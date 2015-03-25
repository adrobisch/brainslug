package brainslug.flow.node.task;

import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.definition.Identifier;
import brainslug.util.Option;

abstract public class AbstractTaskDefinition<SelfType extends AbstractTaskDefinition> extends FlowNodeDefinition<SelfType> {

  protected Class<?> delegateClass;
  protected boolean async;
  protected boolean retryAsync;
  protected CallDefinition methodCall;
  protected Identifier goalId;
  protected RetryStrategy retryStrategy;

  /**
   * sets a delegate class to be executed as action for this task
   *
   * Example:
   * <pre>
   * {@code {@literal @}Override public void define() {
   *   start(event(id(START)))
   *   .execute(task(id(TASK)).delegate(Delegate.class))
   *   .end(event(id(END)));
   *   }
   * }
   * </pre>
   *
   * @param delegateClass type of the delegate class for lookup in the {@link brainslug.flow.context.Registry}
   * @return the task definition
   *
   */
  public SelfType delegate(Class<?> delegateClass) {
    this.delegateClass = delegateClass;
    return self();
  }

  /**
   * sets a method invocation as action for this task
   *
   * Example:
   *
   * <pre>
   * {@code {@literal @}Override public void define() {
   *   start(event(id(START)))
   *   .execute(task(id(TASK)).call(method(TestService.class).name("getString")))
   *   .end(event(id(END)));
   *   }
   * }
   * </pre>
   *
   * @param methodCall the method invocation
   * @return the task definition
   *
   */
  public SelfType call(CallDefinition methodCall) {
    this.methodCall = methodCall;
    return self();
  }

  /**
   * flag the task for async execution. The execution will be paused
   * and continued asynchronously when reaching this task.
   *
   * Example:
   *
   * <pre>
   * {@code {@literal @}Override public void define() {
   *   start(event(id(START)))
   *   .execute(task(id(TASK)).async(true))
   *   .end(event(id(END)));
   *   }
   * }
   * </pre>
   *
   * @param async true if task execution should be async
   * @return the task definition
   *
   */
  public SelfType async(boolean async) {
    this.async = async;
    return self();
  }

  /**
   * flag the task for async execution in case of error. The execution will be paused
   * and continued asynchronously when the execution of this task fails.
   *
   * Be aware that this will not be possible int transactional environments if the transaction
   * is already set to rollback because of the error.
   *
   * Example:
   *
   * <pre>
   * {@code {@literal @}Override public void define() {
   *   start(event(id(START)))
   *   .execute(task(id(TASK)).retryAsync(true))
   *   .end(event(id(END)));
   *   }
   * }
   * </pre>
   *
   * @param retryAsync true if task execution should be scheduled for async retry
   * @return the task definition
   *
   */
  public SelfType retryAsync(boolean retryAsync) {
    this.retryAsync = retryAsync;
    return self();
  }

  /**
   * set the retry strategy in case of async execution.
   *
   * Example:
   *
   * <pre>
   * {@code new FlowBuilder() {
   *   {@literal @}Override
   *   public void define() {
   *   GoalDefinition testGoal = goal(id("aGoal")).check(predicate(goalCondition));
   *
   *   start(id("start"))
   *   .execute(task(id("simpleTask")).retryAsync(true).retryStrategy(retryStrategy))
   *   .end(id("end"));
   *   }
   * }
   * }
   * </pre>
   * @param retryStrategy the retry strategy
   * @return this task definition with retryStrategy defined
   */
  public SelfType retryStrategy(RetryStrategy retryStrategy) {
    this.retryStrategy = retryStrategy;
    return self();
  }

  /**
   * A goal groups tasks by the outcome they contribute to.
   * Tasks with a goal defined will only be executed if the goal is
   * not fulfilled yet.
   *
   * Goals must have a predicate to check the state of fulfilment.
   *
   * Example:
   *
   * <pre>
   * {@code new FlowBuilder() {
   *     {@literal @}Override
   *     public void define() {
   *       GoalDefinition testGoal = goal(id("aGoal")).check(predicate(goalCondition));
   *
   *       start(event(id("START")))
   *       .execute(task(id("TASK"), simpleTask).goal(testGoal))
   *       .end(event(id("END")));
   *     }
   *  }
   * }
   * </pre>
   *
   * @param goal the goal this task belongs to
   * @return this task definition with goal defined
   */
  public SelfType goal(GoalDefinition goal) {
    this.goalId = goal.getId();
    return self();
  }

  public SelfType goal(Identifier goalId) {
    this.goalId = goalId;
    return self();
  }

  public Class<?> getDelegateClass() {
    return delegateClass;
  }

  public CallDefinition getMethodCall() {
    return methodCall;
  }

  public Option<Identifier> getGoal() {
    return Option.of(goalId);
  }

  public boolean isAsync() {
    return async;
  }

  public boolean isRetryAsync() {
    return retryAsync;
  }

  public Option<RetryStrategy> getRetryStrategy() {
    return Option.of(retryStrategy);
  }

}
