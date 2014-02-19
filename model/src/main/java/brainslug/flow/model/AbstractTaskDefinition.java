package brainslug.flow.model;

import brainslug.util.Mixable;

abstract public class AbstractTaskDefinition<T extends AbstractTaskDefinition> extends FlowNodeDefinition<T> {

  private Class<?> delegateClass;
  private boolean async;
  private Mixable marker;
  private CallDefinition methodCall;

  public T delegate(Class<?> delegateClass) {
    this.delegateClass = delegateClass;

    return self();
  }

  public T call(CallDefinition methodCall) {
    this.methodCall = methodCall;

    return self();
  }

  public T async(boolean async) {
    this.async = async;

    return self();
  }

  public T marker(Mixable marker) {
    this.marker = marker;

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

  public boolean isAsync() {
    return async;
  }
}
