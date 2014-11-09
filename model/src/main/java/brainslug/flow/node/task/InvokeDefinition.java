package brainslug.flow.node.task;

import java.lang.reflect.Method;

public class InvokeDefinition extends CallDefinition {

  private final Class<?> serviceClass;
  private Method method;

  public InvokeDefinition(Class<?> serviceClass) {
    this.serviceClass = serviceClass;
  }

  public InvokeDefinition method(Method method) {
    this.method = method;
    return this;
  }

  public InvokeDefinition name(String methodName, Class<?>... parameterTypes) {
    try {
      this.method = serviceClass.getMethod(methodName, parameterTypes);
    } catch (NoSuchMethodException e) {
      String noMethodMessage = String.format("method %s for service %s does not exist", methodName, serviceClass);
      throw new IllegalArgumentException(noMethodMessage);
    }
    return this;
  }

  public Class<?> getTargetClass() {
    return serviceClass;
  }

  public Method getMethod() {
    return method;
  }
}
