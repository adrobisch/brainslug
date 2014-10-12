package brainslug.flow.node.task;

public class HandlerCallDefinition extends CallDefinition {
  Object callee;

  public HandlerCallDefinition(Object callee) {
    this.callee = callee;
  }

  public Object getCallee() {
    return callee;
  }
}
