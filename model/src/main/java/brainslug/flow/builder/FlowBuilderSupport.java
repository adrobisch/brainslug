package brainslug.flow.builder;

import brainslug.flow.EnumIdentifier;
import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import brainslug.flow.StringIdentifier;
import brainslug.flow.expression.*;
import brainslug.flow.node.*;
import brainslug.flow.node.event.AbstractEventDefinition;
import brainslug.flow.node.event.StartEvent;
import brainslug.flow.node.event.timer.StartTimerDefinition;
import brainslug.flow.node.task.*;
import brainslug.flow.path.FlowPathDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FlowBuilderSupport {

  protected FlowDefinition definition;
  protected static ServiceCallInvocationSupport serviceCallInvocation = new ServiceCallInvocationSupport();

  public FlowBuilderSupport() {
  }

  public FlowBuilderSupport withDefinition(FlowDefinition definition) {
    this.definition = definition;
    return this;
  }

  public static Identifier id(Enum id) {
    return new EnumIdentifier(id);
  }

  public static Identifier id(String id) {
    return new StringIdentifier(id);
  }

  public static Identifier id() {
    return new StringIdentifier(generateId());
  }

  protected static String generateId() {
    return UUID.randomUUID().toString();
  }

  /**
   * creates a start timer with given interval and unit
   *
   * @param interval the interval
   * @param intervalUnit the unit
   * @return a start timer to be used for a start event definition
   */
  public StartTimerDefinition every(int interval, TimeUnit intervalUnit) {
    return new StartTimerDefinition(interval, intervalUnit);
  }

  /**
   * create a start event with the given id.
   *
   * @param startId the start event id
   *
   * @return the flow path starting with this event
   */
  public FlowPathDefinition start(Identifier startId) {
    return start(event(startId));
  }

  /**
   * create a start event with the given id,
   * which will be started every interval with
   * the duration given in the timer definition.
   *
   * @param startId the start event id
   * @param startTimerDefinition the timer definition defining the interval
   *
   * @return the flow path starting with this event
   */
  public FlowPathDefinition start(Identifier startId, StartTimerDefinition startTimerDefinition) {
    return start(event(startId), startTimerDefinition);
  }

  public FlowPathDefinition start(AbstractEventDefinition event) {
    definition.addNode(startEvent(event));
    return new FlowPathDefinition(definition, event);
  }

  /**
   * create a start event with the given event,
   * which will be started every interval with
   * the duration given in the timer definition.
   *
   * @param event the start event id
   * @param startTimerDefinition the timer definition defining the interval
   *
   * @return the flow path starting with this event
   */
  public FlowPathDefinition start(AbstractEventDefinition event, StartTimerDefinition startTimerDefinition) {
    definition.addNode(startTimerEvent(event, startTimerDefinition));
    return new FlowPathDefinition(definition, event);
  }

  protected AbstractEventDefinition startEvent(AbstractEventDefinition event) {
    event.with(new StartEvent());
    return event;
  }

  protected AbstractEventDefinition startTimerEvent(AbstractEventDefinition event, StartTimerDefinition startTimerDefinition) {
    event.with(new StartEvent().withRecurringTimerDefinition(startTimerDefinition));
    return event;
  }

  /**
   * define task as first node in the flow
   * @param task the task
   * @return the flow path starting with this task
   */
  public FlowPathDefinition start(AbstractTaskDefinition task) {
    definition.addNode(task);
    return new FlowPathDefinition(definition, task);
  }

  public FlowPathDefinition start(Identifier id, AbstractTaskDefinition task, StartTimerDefinition startTimerDefinition) {
    return start(event(id), startTimerDefinition).execute(task);
  }

  /**
   * create a flow path for execution after of the node id
   *
   * @param id of the node to continue after
   * @return the flow after the given node
   */
  public FlowPathDefinition after(Identifier id) {
    return new FlowPathDefinition(definition, definition.getNode(id));
  }

  /**
   * create a flow path for execution after of the given event id
   *
   * @param id of the event to continue after
   * @return the flow after the given event
   */
  public FlowPathDefinition on(Identifier id) {
    return new FlowPathDefinition(definition, definition.getNode(id, AbstractEventDefinition.class));
  }

  /**
   * create a flow path merging the execution after the given nodes
   * by introducing a new merge node to which the nodes connect.
   *
   * execution will continue for every token that triggers this merge node.
   *
   * Example:
   *
   * <pre>
   * {@code {@literal @}Override public void define() {
      start(event(id(START)))
      .choice(id(CHOICE))
      .when(eq(constant(x) ,"test")).execute(task(id(TASK)))
      .or()
      .when(eq(constant(x), "test2")).execute(task(id(TASK2)));

      merge(id(MERGE), id(TASK), id(TASK2))
      .end(event(id(END)));
      }
   * }
   * </pre>
   *
   * @param mergeId the id of the new merge node
   * @param ids of the nodes to be merged
   * @return the flow path beginning with the merge node
   */
  public FlowPathDefinition merge(Identifier mergeId, Identifier... ids) {
    MergeDefinition mergeDefinition = new MergeDefinition().id(mergeId);
    definition.addNode(mergeDefinition);
    connectToNode(mergeDefinition, Arrays.asList(ids));
    return new FlowPathDefinition(definition, mergeDefinition);
  }

  public FlowPathDefinition merge(FlowNodeDefinition<?>... nodeDefinitions) {
    return merge(id(generateId()), nodeDefinitions);
  }

  public FlowPathDefinition merge(Identifier id, FlowNodeDefinition<?>... nodeDefinitions) {
    MergeDefinition mergeDefinition = new MergeDefinition().id(id);
    definition.addNode(mergeDefinition);
    connectToNode(mergeDefinition, idList(nodeDefinitions));
    return new FlowPathDefinition(definition, mergeDefinition);
  }

  /**
   * create a flow path joining the execution after the given nodes
   * by introducing a new merge join to which the nodes connect.
   *
   * execution will continue only if a token exists for
   * every joined node during the execution of the join node.
   *
   * Example:
   *
   * <pre>
   * {@code {@literal @}Override public void define() {
      start(event(id(StartEvent)))
      .parallel(id(Parallel))
      .execute(task(id(SecondTask)))
      .and()
      .execute(task(id(ThirdTask)));

      join(id(Join), id(SecondTask), id(ThirdTask))
      .end(event(id(EndEvent2)));
      }
   * }
   * </pre>
   *
   * @param joinId the id of the new join node
   * @param ids of the nodes to be merged
   * @return the flow path beginning with the join node
   */
  public FlowPathDefinition join(Identifier joinId, Identifier... ids) {
    JoinDefinition joinDefinition = new JoinDefinition().id(joinId);
    definition.addNode(joinDefinition);
    connectToNode(joinDefinition, Arrays.asList(ids));
    return new FlowPathDefinition(definition, joinDefinition);
  }

  public FlowPathDefinition join(FlowNodeDefinition<?>... flowNodeDefinitions) {
    return join(id(generateId()), flowNodeDefinitions);
  }

  public FlowPathDefinition join(Identifier joinId, FlowNodeDefinition<?>... flowNodeDefinitions) {
    JoinDefinition joinDefinition = new JoinDefinition().id(joinId);
    definition.addNode(joinDefinition);
    connectToNode(joinDefinition, idList(flowNodeDefinitions));
    return new FlowPathDefinition(definition, joinDefinition);
  }

  List<Identifier> idList(FlowNodeDefinition<?>... flowNodeDefinitions) {
    List<Identifier> identifiers = new ArrayList<Identifier>();
    for (FlowNodeDefinition<?> nodeDefinition: flowNodeDefinitions) {
      identifiers.add(nodeDefinition.getId());
    }
    return identifiers;
  }

  private void connectToNode(FlowNodeDefinition node, List<Identifier> idsToConnect) {
    for (Identifier id : idsToConnect) {
      definition.getNode(id).addOutgoing(node);
      definition.getNode(node.getId()).addIncoming(definition.getNode(id));
    }
  }

  public static TaskDefinition task(Identifier id) {
    return new TaskDefinition().id(id).display(id.toString());
  }

  /**
   * create a task definition with the given task to be executed
   *
   * Example:
   *
   * <pre>
   * {@code {@literal @}Override
      public void define() {
        Task callee = new Task() {
          {@literal @}Override
          public void execute(ExecutionContext o) {
          }
        };

        start(event(id("start")), every(5, TimeUnit.SECONDS))
        .execute(task(id("task"), callee));
      }
   * }
   * </pre>
   *
   * @param id the task id
   * @param callee the callee task
   * @return the task definition with the given task
   */
  public static TaskDefinition task(Identifier id, Task callee) {
    return new TaskDefinition().id(id).display(id.toString()).call(new HandlerCallDefinition(callee));
  }

  public static EventDefinition event(Identifier id) {
    return new EventDefinition().id(id).display(id.toString());
  }

  public static <T> Expression expression(T expression) {
    return new Expression<T>(expression);
  }

  public static <T> Expression<T> constant(T constantValue) {
    return expression(constantValue);
  }

  public static Property property(Identifier id) {
    return new Property(id);
  }

  public static <T> T property(Identifier id, Class<T> clazz) {
    return (T) val(new Property(id));
  }

  /**
   * add a parameter to the invocation arguments of a proxy method call definition.
   *
   * @param expression the parameter expression to add
   * @param <T> the type of the expression
   * @return null of type T
   */
  public static <T> T val(Expression<T> expression) {
    serviceCallInvocation.argument(expression);
    return null;
  }

  public static EqualDefinition<Expression, Expression<Object>> eq(Expression actual, Object expected) {
    return new PredicateBuilder<Expression>(actual).isEqualTo(expected);
  }

  public static TrueDefinition<Expression> isTrue(Expression actual) {
    return new PredicateBuilder<Expression>(actual).isTrue();
  }

  public static <T extends Predicate> PredicateDefinition<T> predicate(T predicate) {
    return new PredicateDefinition<T>(predicate);
  }

  public static InvokeDefinition method(Class<?> clazz) {
    return new InvokeDefinition(clazz);
  }

  public CallDefinition method(Object returnValueOfProxyInvocation) {
    return serviceCallInvocation.createCallDefinitionFromCurrentStack();
  }

  /**
   * create a service proxy to be used for type-safe call definitions.
   *
   * Example:
   *
   * <pre>
   * {@code {@literal @}Override
      public void define() {
      TestService testService = service(TestService.class);

      start(event(id(START)))
      .execute(task(id(TASK)).call(method(testService.echo(testService.getString()))))
      .end(event(id(END)));

      }
   * }
   * </pre>
   *
   * @param clazz the interface type to proxy
   * @param <T> the type of the interface
   * @return a service proxy of type T
   */
  public static <T> T service(Class<T> clazz) {
    return serviceCallInvocation.createServiceProxy(clazz);
  }

  public PredicateBuilder<CallDefinition> resultOf(CallDefinition methodCall) {
    return new PredicateBuilder<CallDefinition>(methodCall);
  }

  /**
   * create a goal definition with the given id.
   *
   * Example:
   *
   * <pre>
   * {@code {@literal @}Override
      public void define() {
        start(id("start"))
         .execute(task(id("simpleTask"))
         .retryAsync(true)
         .goal(id("taskExecuted")))
        .end(id("end"));

        goal(id("taskExecuted")).check(predicate(new GoalPredicate<Void>() {
          {@literal @}Override
          public boolean isFulfilled(Void aVoid) {
            return true;
          }
        }));
      }
   * }
   * </pre>
   *
   * @param id the goal id
   * @return the goal definition
   */
  public GoalDefinition goal(Identifier id) {
    return new GoalDefinition(definition).id(id);
  }

  public GoalDefinition check(Identifier id, PredicateDefinition goalPredicate) {
    return new GoalDefinition(definition).id(id).check(goalPredicate);
  }

  public GoalDefinition check(Identifier id, Predicate<?> goalPredicate) {
    return new GoalDefinition(definition).id(id).check(new PredicateDefinition<Predicate>(goalPredicate));
  }

}
