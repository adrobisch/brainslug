package brainslug.flow.model;

import java.util.UUID;

abstract public class FlowBuilder extends FlowBuilderSupport {

  boolean isDefined = false;
  String id;

  public FlowBuilder() {
    super(new FlowDefinition());
    definition.id(new StringIdentifier(getId())).name(getName());
  }

  abstract public void define();

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
    if (!isDefined) {
      define();
      isDefined = true;
    }
    return definition;
  }
}
