package brainslug.flow.execution.impl;

import brainslug.flow.execution.Execute;
import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.model.AbstractTaskDefinition;
import brainslug.flow.model.FlowNodeDefinition;
import brainslug.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.List;

public class TaskNodeExecutor extends DefaultNodeExecutor<AbstractTaskDefinition> {
  @Override
  public List<FlowNodeDefinition> execute(AbstractTaskDefinition taskDefinition, ExecutionContext context) {
    if (taskDefinition.getDelegateClass() != null) {
      executeDelegate(taskDefinition, context);
    }
    return takeAll(taskDefinition);
  }

  private void executeDelegate(AbstractTaskDefinition taskDefinition, ExecutionContext context) {
    Object serviceInstance = context.getBrainslugContext().getRegistry().getService(taskDefinition.getDelegateClass());
    Method executeMethod = ReflectionUtil.getFirstMethodAnnotatedWith(taskDefinition.getDelegateClass(), Execute.class);
    invokeServiceMethod(context, serviceInstance, executeMethod);
  }

  private void invokeServiceMethod(ExecutionContext context, Object serviceInstance, Method executeMethod) {
    try {
      executeMethod.invoke(serviceInstance, context);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
