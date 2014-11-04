package brainslug.flow.node.task;

import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.Identifier;
import brainslug.util.Mixable;
import brainslug.util.Option;

abstract public class AbstractTaskDefinition<SelfType extends AbstractTaskDefinition> extends FlowNodeDefinition<SelfType> {

  protected Class<?> delegateClass;
  protected boolean async;
  protected boolean retryAsync;
  protected Mixable marker;
  protected CallDefinition methodCall;
  protected Identifier goalId;
  protected RetryStrategy retryStrategy;

  public SelfType delegate(Class<?> delegateClass) {
    this.delegateClass = delegateClass;

    return self();
  }

  public SelfType call(CallDefinition methodCall) {
    this.methodCall = methodCall;

    return self();
  }

  public SelfType async(boolean async) {
    this.async = async;

    return self();
  }

  public SelfType retryAsync(boolean retryAsync) {
    this.retryAsync = retryAsync;

    return self();
  }

  public SelfType retryStrategy(RetryStrategy retryStrategy) {
    this.retryStrategy = retryStrategy;
    return self();
  }

  public SelfType marker(Mixable marker) {
    this.marker = marker;

    return self();
  }

  /**
   * A goal groups tasks by the outcome they contribute to.
   * Tasks with a goal defined will only be executed if the goal is
   * not fulfilled yet.
   *
   * Goals must have a predicate to check the state of fulfilment.
   * <p>
   * Example:
   *
   * <pre>
   * {@code new FlowBuilder() {
        @Override
        public void define() {
          GoalDefinition testGoal = goal(id("aGoal")).check(predicate(goalCondition));

          start(event(id(START)))
          .execute(task(id(TASK), simpleTask).goal(testGoal))
          .end(event(id(END)));
        }
     }
   * }
   * </pre>
   * </p>
   * @param goal the goal this task belongs to
   * @return this event definition with goal defined
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

  public Mixable getMarker() {
    return marker;
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
