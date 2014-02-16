package brainslug.flow.model;

public class ServiceCallDefinition extends CallDefinition {

  private final Class<?> serviceClass;
  private String methodName;

  public ServiceCallDefinition(Class<?> serviceClass) {
    this.serviceClass = serviceClass;
  }

  public ServiceCallDefinition method(String methodName) {
    this.methodName = methodName;
    return this;
  }

  public Class<?> getServiceClass() {
    return serviceClass;
  }

  public String getMethodName() {
    return methodName;
  }
}
