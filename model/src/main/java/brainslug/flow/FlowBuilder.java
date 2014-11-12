package brainslug.flow;

import brainslug.flow.builder.FlowBuilderSupport;

import java.util.UUID;

/**
 * The FlowBuilder is used to create new {@link brainslug.flow.FlowDefinition}s
 * by implementing the define method and using the DSL methods provided by the
 * {@link brainslug.flow.builder.FlowBuilderSupport}.
 */
abstract public class FlowBuilder extends FlowBuilderSupport {

  String id;

  public FlowBuilder() {
  }

  abstract public void define();

  public void flowId(Identifier id) {
    this.id = id.stringValue();
  }

  public String getId() {
    if (id == null) {
      id = generateId();
    }
    return id;
  }

  protected String generateId() {
    return UUID.randomUUID().toString();
  }

  public String getName() {
    return getId();
  }

  public FlowDefinition getDefinition() {
    withDefinition(new FlowDefinition());
    define();
    definition.id(new StringIdentifier(getId())).name(getName());

    return definition;
  }
}
