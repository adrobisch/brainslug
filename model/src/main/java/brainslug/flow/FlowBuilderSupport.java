package brainslug.flow;

import brainslug.flow.expression.*;
import brainslug.flow.node.event.AbstractEventDefinition;
import brainslug.flow.node.event.timer.StartTimerDefinition;
import brainslug.flow.node.event.StartEvent;
import brainslug.flow.node.*;
import brainslug.flow.path.FlowPathDefinition;
import brainslug.flow.node.task.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FlowBuilderSupport {

  protected FlowDefinition definition;

  public FlowBuilderSupport() {
  }

  public FlowBuilderSupport withDefinition(FlowDefinition definition) {
    this.definition = definition;
    return this;
  }

  public Identifier id(Enum id) {
    return new EnumIdentifier(id);
  }

  public Identifier id(String id) {
    return new StringIdentifier(id);
  }

  public FlowPathDefinition start(Identifier startId) {
    return start(event(startId));
  }

  public FlowPathDefinition start(AbstractEventDefinition event) {
    event.with(new StartEvent());
    definition.addNode(event);
    return new FlowPathDefinition(definition, event);
  }

  public FlowPathDefinition start(AbstractEventDefinition event, StartTimerDefinition startTimerDefinition) {
    event.with(new StartEvent().withRecurringTimerDefinition(startTimerDefinition));
    definition.addNode(event);
    return new FlowPathDefinition(definition, event);
  }

  public FlowPathDefinition start(AbstractTaskDefinition task) {
    definition.addNode(task);
    return new FlowPathDefinition(definition, task);
  }

  public FlowPathDefinition after(Identifier id) {
    return new FlowPathDefinition(definition, definition.getNode(id));
  }

  public FlowPathDefinition on(Identifier id) {
    return new FlowPathDefinition(definition, definition.getNode(id, AbstractEventDefinition.class));
  }

  public FlowPathDefinition merge(Identifier mergeId, Identifier... ids) {
    MergeDefinition mergeDefinition = new MergeDefinition().id(mergeId);
    definition.addNode(mergeDefinition);
    connectToNode(mergeDefinition, Arrays.asList(ids));
    return new FlowPathDefinition(definition, mergeDefinition);
  }

  /**
   * TODO: think about a better argument list here, its pretty confusing
   * and easy to forget the join ids
   * @param joinId
   * @param ids
   * @return
   */
  public FlowPathDefinition join(Identifier joinId, Identifier... ids) {
    JoinDefinition joinDefinition = new JoinDefinition().id(joinId);
    definition.addNode(joinDefinition);
    connectToNode(joinDefinition, Arrays.asList(ids));
    return new FlowPathDefinition(definition, joinDefinition);
  }

  private void connectToNode(FlowNodeDefinition node, List<Identifier> idsToConnect) {
    for (Identifier id : idsToConnect) {
      definition.getNode(id).addOutgoing(node);
      definition.getNode(node.getId()).addIncoming(definition.getNode(id));
    }
  }

  public TaskDefinition task(Identifier id) {
    return new TaskDefinition().id(id).display(id.toString());
  }

  public TaskDefinition task(Identifier id, Task callee) {
    return new TaskDefinition().id(id).display(id.toString()).call(new HandlerCallDefinition(callee));
  }

  public EventDefinition event(Identifier id) {
    return new EventDefinition().id(id).display(id.toString());
  }

  public StartTimerDefinition every(int interval, TimeUnit intervalUnit) {
    return new StartTimerDefinition(interval, intervalUnit);
  }

  public <T> PredicateBuilder<Expression> expression(T expression) {
    return new PredicateBuilder<Expression>(new Expression<T>(expression));
  }

  public <T> PredicateBuilder<Expression> constant(T expression) {
    return expression(expression);
  }

  public <T extends Identifier> PredicateBuilder<Property> property(T expression) {
    return new PredicateBuilder<Property>(new Property(expression));
  }

  public <T extends Predicate> PredicateDefinition<T> predicate(T predicate) {
    return new PredicateDefinition<T>(predicate);
  }

  public ServiceCallDefinition service(Class<?> clazz) {
    return new ServiceCallDefinition(clazz);
  }

  public HandlerCallDefinition run(Object callee) {
    return new HandlerCallDefinition(callee);
  }

  public PredicateBuilder<CallDefinition> resultOf(CallDefinition methodCall) {
    return new PredicateBuilder<CallDefinition>(methodCall);
  }

  public GoalDefinition goal(Identifier id) {
    return new GoalDefinition(definition).id(id);
  }

  public GoalDefinition check(Identifier id, PredicateDefinition goalPredicate) {
    return new GoalDefinition(definition).id(id).check(goalPredicate);
  }

}
