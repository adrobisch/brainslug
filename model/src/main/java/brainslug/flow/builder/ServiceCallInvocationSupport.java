package brainslug.flow.builder;

import brainslug.flow.expression.Expression;
import brainslug.flow.node.task.CallDefinition;
import brainslug.flow.node.task.InvokeDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Stack;

public class ServiceCallInvocationSupport {

  Stack<ProxyStackEntry> proxyStack = new Stack<ProxyStackEntry>();

  public void argument(Expression expression) {
    proxyStack.push(new ParameterEntry<Expression>(expression));
  }

  public CallDefinition createCallDefinitionFromCurrentStack() {
    if (proxyStack.empty()) {
      throw new IllegalStateException("no service invocation given. you must define one first...");
    }

    ServiceCallInvocationSupport.ProxyStackEntry lastInvocation = proxyStack.pop();

    if (lastInvocation instanceof ServiceCallInvocationSupport.ServiceInvocationEntry) {
      proxyStack.clear();
      return (ServiceCallInvocationSupport.ServiceInvocationEntry) lastInvocation;
    } else {
      throw new IllegalStateException("you must define a valid service invocation.");
    }
  }

  public interface ProxyStackEntry {
  }

  public static class ServiceInvocationEntry extends InvokeDefinition implements ProxyStackEntry {
    public ServiceInvocationEntry(Class<?> serviceClass) {
      super(serviceClass);
    }
  }

  public class ParameterEntry<T> implements ProxyStackEntry {
    T value;

    ParameterEntry(T value) {
      this.value = value;
    }

    public T getValue() {
      return value;
    }
  }

  public boolean empty() {
    return proxyStack.empty();
  }

  public <T> T createServiceProxy(final Class<T> clazz) {
    InvocationHandler handler = new InvocationHandler() {
      @Override
      public Object invoke(Object o, Method method, Object[] arguments) throws Throwable {
        if (method.getDeclaringClass().getName().equals(Object.class.getName())) {
          // dont create proxyStack entry for toString etc. during debugging
          return null;
        }

        ServiceCallInvocationSupport.ServiceInvocationEntry invocation = new ServiceCallInvocationSupport.ServiceInvocationEntry(clazz);
        invocation.method(method);

        proxyStack.add(withArgumentsFromStack(invocation));
        return null;
      }
    };

    return (T) createProxyInstance(clazz, handler);
  }

  private ServiceCallInvocationSupport.ServiceInvocationEntry withArgumentsFromStack(ServiceCallInvocationSupport.ServiceInvocationEntry invocation) {
    for (int paramIndex = 0; paramIndex < invocation.getMethod().getParameterTypes().length; paramIndex++) {
      invocation.arg(proxyStack.pop());
    }

    return invocation;
  }

  protected <T> Object createProxyInstance(Class<T> clazz, InvocationHandler handler) {
    return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{clazz}, handler);
  }

}
