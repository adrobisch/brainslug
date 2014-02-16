package brainslug.flow.model;

import brainslug.flow.model.expression.PredicateBuilder;
import brainslug.flow.model.expression.Constant;
import brainslug.flow.model.expression.Expression;
import brainslug.flow.model.marker.StartEvent;

import java.util.Arrays;
import java.util.List;

public class FlowBuilderSupport {

  protected FlowDefinition definition;

  public FlowBuilderSupport(FlowDefinition definition) {
    this.definition = definition;
  }

  public Identifier id(Enum id) {
    return new EnumIdentifier(id);
  }

  public Identifier id(String id) {
    return new StringIdentifier(id);
  }

  public FlowPathDefinition start(EventDefinition event) {
    event.with(new StartEvent());
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
    return new FlowPathDefinition(definition, definition.getNode(id, EventDefinition.class));
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

  public EventDefinition event(Identifier id) {
    return new EventDefinition().id(id).display(id.toString());
  }

  public <T> PredicateBuilder<Constant<T>> constant(T value) {
    return new PredicateBuilder<Constant<T>>(new Constant<T>(value));
  }

  public <T> PredicateBuilder<Expression> expression(T expression) {
    return new PredicateBuilder<Expression>(new Expression<T>(expression));
  }

  public ServiceCallDefinition service(Class<?> clazz) {
    return new ServiceCallDefinition(clazz);
  }

  public HandlerCallDefinition handler(Object callee) {
    return new HandlerCallDefinition(callee);
  }

  public PredicateBuilder<CallDefinition> resultOf(CallDefinition methodCall) {
    return new PredicateBuilder<CallDefinition>(methodCall);
  }
}
