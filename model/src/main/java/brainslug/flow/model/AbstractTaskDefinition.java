package brainslug.flow.model;

import brainslug.util.Mixable;

abstract public class AbstractTaskDefinition<T extends AbstractTaskDefinition> extends FlowNodeDefinition<T> {

  private Class<?> delegateClass;
  private boolean async;
  private Mixable mixable;
  private MethodCallDefinition methodCall;

  public AbstractTaskDefinition delegate(Class<?> delegateClass) {
    this.delegateClass = delegateClass;

    return self();
  }

  public AbstractTaskDefinition call(MethodCallDefinition methodCall) {
    this.methodCall = methodCall;

    return self();
  }

  public AbstractTaskDefinition async(boolean async) {
    this.async = async;

    return self();
  }

  public AbstractTaskDefinition marker(Mixable mixable) {
    this.mixable = mixable;

    return self();
  }

  public Class<?> getDelegateClass() {
    return delegateClass;
  }

  public MethodCallDefinition getMethodCall() {
    return methodCall;
  }

  public Mixable getMixable() {
    return mixable;
  }

  public boolean isAsync() {
    return async;
  }
}
