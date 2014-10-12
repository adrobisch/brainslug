package brainslug.flow;

import java.util.UUID;

abstract public class FlowBuilder extends FlowBuilderSupport {

  String id;

  public FlowBuilder() {
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
    withDefinition(new FlowDefinition().id(new StringIdentifier(getId())).name(getName()));
    define();
    return definition;
  }
}
