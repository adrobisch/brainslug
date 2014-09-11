package brainslug.flow.execution.impl;

import brainslug.flow.execution.Execute;
import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.SimpleTask;
import brainslug.flow.model.AbstractTaskDefinition;
import brainslug.flow.model.FlowNodeDefinition;
import brainslug.flow.model.HandlerCallDefinition;
import brainslug.flow.model.ServiceCallDefinition;
import brainslug.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TaskNodeExecutor extends DefaultNodeExecutor<AbstractTaskDefinition> {
  @Override
  public List<FlowNodeDefinition> execute(AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    removeTriggerToken(execution);

    if(taskDefinition.isAsync() && execution.getTrigger().getSourceNodeId() != null) {
      execution.getBrainslugContext().getAsyncTaskScheduler()
        .scheduleTask(taskDefinition.getId(), execution.getTrigger().getSourceNodeId(), execution.getTrigger().getInstanceId(), execution.getTrigger().getDefinitionId());
      return takeNone();
    } else if (taskDefinition.getDelegateClass() != null) {
      Object delegateInstance = execution.getBrainslugContext().getRegistry().getService(taskDefinition.getDelegateClass());
      executeDelegate(delegateInstance, execution);
    } else if (taskDefinition.getMethodCall() instanceof HandlerCallDefinition) {
      final Object callee = ((HandlerCallDefinition) taskDefinition.getMethodCall()).getCallee();
      executeDelegate(callee, execution);
    } else if (taskDefinition.getMethodCall() instanceof ServiceCallDefinition) {
      executeServiceMethodCall(taskDefinition, (ServiceCallDefinition) taskDefinition.getMethodCall(), execution);
    }

    return takeAll(taskDefinition);
  }

  protected void executeServiceMethodCall(AbstractTaskDefinition taskDefinition, ServiceCallDefinition serviceCall, ExecutionContext context) {
    Class<?> serviceClass = serviceCall.getServiceClass();
    Object serviceInstance = context.getBrainslugContext().getRegistry().getService(serviceClass);
    invokeServiceMethodWithArguments(taskDefinition, serviceClass, serviceCall.getMethodName(), serviceInstance);
  }

  protected void invokeServiceMethodWithArguments(AbstractTaskDefinition taskDefinition, Class<?> serviceClass, String methodName, Object serviceInstance) {
    try {
      Method serviceMethod = serviceClass.getMethod(methodName);
      serviceMethod.invoke(serviceInstance, taskDefinition.getMethodCall().getArguments().toArray());
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  protected void executeDelegate(Object delegateInstance, ExecutionContext context) {
    if (delegateInstance instanceof SimpleTask) {
      ((SimpleTask) delegateInstance).execute(context);
    } else {
      Method executeMethod = ReflectionUtil.getFirstMethodAnnotatedWith(delegateInstance.getClass(), Execute.class);
      invokeServiceMethodWithContext(context, delegateInstance, executeMethod);
    }
  }

  protected void invokeServiceMethodWithContext(ExecutionContext context, Object serviceInstance, Method executeMethod) {
    try {
      executeMethod.setAccessible(true);
      executeMethod.invoke(serviceInstance, executionArguments(context, executeMethod).toArray());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected List<Object> executionArguments(ExecutionContext context, Method executeMethod) {
    List<Object> arguments = new ArrayList<Object>();
    for (Class<?> parameterType : executeMethod.getParameterTypes()) {
      if (parameterType.isAssignableFrom(ExecutionContext.class)) {
        arguments.add(context);
      } else {
        arguments.add(context.getBrainslugContext().getRegistry().getService(parameterType));
      }
    }
    return arguments;
  }
}
