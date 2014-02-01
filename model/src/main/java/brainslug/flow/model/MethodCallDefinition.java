package brainslug.flow.model;

import java.util.ArrayList;
import java.util.List;

public class MethodCallDefinition {
  private final Class<?> serviceClass;
  private String methodName;
  private List<Object> arguments = new ArrayList<Object>();

  public MethodCallDefinition(Class<?> serviceClass) {
    this.serviceClass = serviceClass;
  }

  public MethodCallDefinition method(String methodName) {
    this.methodName = methodName;
    return this;
  }

  public MethodCallDefinition arg(Object argument) {
    arguments.add(argument);
    return this;
  }

  public Class<?> getServiceClass() {
    return serviceClass;
  }

  public String getMethodName() {
    return methodName;
  }

  public List<Object> getArguments() {
    return arguments;
  }
}
