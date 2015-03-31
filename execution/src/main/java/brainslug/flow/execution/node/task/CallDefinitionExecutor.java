package brainslug.flow.execution.node.task;

import brainslug.flow.builder.ServiceCallInvocationSupport;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.expression.Expression;
import brainslug.flow.expression.Property;
import brainslug.flow.node.task.CallDefinition;
import brainslug.flow.node.task.HandlerCallDefinition;
import brainslug.flow.node.task.InvokeDefinition;
import brainslug.flow.node.task.Task;
import brainslug.util.Option;
import brainslug.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CallDefinitionExecutor {

  public Object execute(CallDefinition callDefinition, ExecutionContext execution) {
    if (callDefinition instanceof HandlerCallDefinition) {
      final Object callee = ((HandlerCallDefinition) callDefinition).getCallee();
      return executeDelegate(callee, execution);
    } else if (callDefinition instanceof InvokeDefinition) {
      return invokeMethod((InvokeDefinition) callDefinition, execution);
    }
    throw new IllegalArgumentException("unable to execute call: " + callDefinition);
  }

  protected Object invokeMethod(InvokeDefinition method, ExecutionContext context) {
    Class<?> targetClass = method.getTargetClass();
    Object serviceInstance = context.service(targetClass);
    return invokeMethodWithArguments(method.getMethod(), serviceInstance, getArguments(method, context));
  }

  protected Object invokeMethodWithArguments(Method serviceMethod, Object serviceInstance, Object[] arguments) {
    try {
      return serviceMethod.invoke(serviceInstance, arguments);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected Object[] getArguments(CallDefinition callDefinition, ExecutionContext executionContext) {
    List<Object> arguments = new ArrayList<Object>();

    for (Object argument: callDefinition.getArguments()) {
      if (argument instanceof CallDefinition) {
        arguments.add(execute((CallDefinition) argument, executionContext));
      } else if(argument instanceof ServiceCallInvocationSupport.ParameterEntry) {
        Object parameterValue = getParameterValue((ServiceCallInvocationSupport.ParameterEntry) argument, executionContext);
        arguments.add(parameterValue);
      } else {
        throw new IllegalArgumentException(String.format("unknown parameter type %s", argument.getClass().getName()));
      }
    }

    return arguments.toArray();
  }

  private Object getParameterValue(ServiceCallInvocationSupport.ParameterEntry argument, ExecutionContext executionContext) {
    Object value = argument.getValue();

    if (value instanceof Property) {
      return executionContext.property(((Property) value).getValue(), Object.class);
    } else if (value instanceof Expression) {
      return ((Expression) value).getValue();
    }
    throw new IllegalArgumentException("unknown parameter value type: " + value.getClass().getName());
  }

  protected Object executeDelegate(Object delegateInstance, ExecutionContext context) {
    Option<Method> executeAnnotatedMethod = ReflectionUtil.getFirstMethodAnnotatedWith(delegateInstance.getClass(), Execute.class);

    if (executeAnnotatedMethod.isPresent()) {
      return invokeDelegateMethod(context, delegateInstance, executeAnnotatedMethod.get());
    } else if (delegateInstance instanceof Task) {
      ((Task) delegateInstance).execute(context);
      return null;
    } else {
      throw new IllegalArgumentException(unsupportedDelegateMessage(delegateInstance));
    }
  }

  protected String unsupportedDelegateMessage(Object delegateInstance) {
    return "unsupported delegate class: " + delegateInstance.getClass()
      + " use " + Task.class.getName() + " or " + SimpleTask.class.getName();
  }

  protected Object invokeDelegateMethod(ExecutionContext context, Object serviceInstance, Method executeMethod) {
    try {
      executeMethod.setAccessible(true);
      return executeMethod.invoke(serviceInstance, contextExecutionArguments(context, executeMethod).toArray());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected List<Object> contextExecutionArguments(ExecutionContext context, Method executeMethod) {
    List<Object> arguments = new ArrayList<Object>();
    for (Class<?> parameterType : executeMethod.getParameterTypes()) {
      if (parameterType.isAssignableFrom(ExecutionContext.class)) {
        arguments.add(context);
      } else {
        arguments.add(context.service(parameterType));
      }
    }
    return arguments;
  }

}
