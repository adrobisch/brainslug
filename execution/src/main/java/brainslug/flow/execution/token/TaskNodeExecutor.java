package brainslug.flow.execution.token;

import brainslug.flow.execution.*;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.*;
import brainslug.flow.FlowDefinition;
import brainslug.flow.execution.async.AsyncTriggerErrorDetails;
import brainslug.flow.expression.PredicateDefinition;
import brainslug.flow.node.task.*;
import brainslug.util.Option;
import brainslug.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TaskNodeExecutor extends DefaultNodeExecutor<AbstractTaskDefinition> {

  private Logger log = LoggerFactory.getLogger(TaskNodeExecutor.class);

  @Override
  public brainslug.flow.execution.FlowNodeExecutionResult execute(AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    consumeAllNodeTokens(execution.getTrigger());

    if (taskDefinition.getGoal().isPresent() && goalIsFulfilled((Identifier) taskDefinition.getGoal().get(), execution)) {
      return takeAll(taskDefinition);
    } else if (taskDefinition.isAsync() && !execution.getTrigger().isAsync()) {
      scheduleAsyncTask(taskDefinition, execution);
      return takeNone();
    } else if (isExecutable(taskDefinition)) {
      return executeWithOptionalAsyncRetry(taskDefinition, execution);
    } else {
      log.warn("executing task node without execution definition, " +
        "please specify the task node execution by using a delegate class or call definition to actually do something in this task");
      return takeAll(taskDefinition);
    }
  }

  protected FlowNodeExecutionResult executeWithOptionalAsyncRetry(AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    try {
      if (Option.of(taskDefinition.getDelegateClass()).isPresent()) {
        return executeDelegateClass(taskDefinition, execution);
      } else if (Option.of(taskDefinition.getMethodCall()).isPresent()) {
        return executeMethodCall(taskDefinition, execution);
      }
      throw new IllegalStateException("this method should only be called with executable " + taskDefinition);
    } catch (Exception e) {
      log.error(String.format("error during task (%s) execution: ", taskDefinition), e);
      if (taskDefinition.isRetryAsync()) {
        return scheduleRetry(e, taskDefinition, execution);
      }
      return takeNone();
    }
  }

  private FlowNodeExecutionResult scheduleRetry(Exception e, AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    execution.getBrainslugContext().getAsyncTriggerScheduler()
      .schedule(
        new AsyncTrigger()
          .incrementRetries()
          .withErrorDetails(new AsyncTriggerErrorDetails(e))
          .withNodeId(taskDefinition.getId())
          .withInstanceId(execution.getTrigger().getInstanceId())
          .withDefinitionId(execution.getTrigger().getDefinitionId())
      );
    return new FlowNodeExecutionResult().failed(true);
  }

  protected boolean isExecutable(AbstractTaskDefinition taskDefinition) {
    return taskDefinition.getDelegateClass() != null || taskDefinition.getMethodCall() != null;
  }

  protected FlowNodeExecutionResult executeMethodCall(AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    if (taskDefinition.getMethodCall() instanceof HandlerCallDefinition) {
      final Object callee = ((HandlerCallDefinition) taskDefinition.getMethodCall()).getCallee();
      executeDelegate(callee, execution);
      return takeAll(taskDefinition);
    } else if (taskDefinition.getMethodCall() instanceof ServiceCallDefinition) {
      executeServiceMethodCall(taskDefinition, (ServiceCallDefinition) taskDefinition.getMethodCall(), execution);
      return takeAll(taskDefinition);
    }
    throw new IllegalArgumentException("unable to execute method call: " + taskDefinition.getMethodCall());
  }

  private FlowNodeExecutionResult executeDelegateClass(AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    Object delegateInstance = execution.getBrainslugContext().getRegistry().getService(taskDefinition.getDelegateClass());
    executeDelegate(delegateInstance, execution);
    return takeAll(taskDefinition);
  }

  protected boolean goalIsFulfilled(Identifier goalId, ExecutionContext execution) {
    FlowDefinition definition = execution
      .getBrainslugContext()
      .getDefinitionStore()
      .findById(execution.getTrigger().getDefinitionId());

    Option<PredicateDefinition> goalPredicate = definition.getGoalPredicate(goalId);

    if (!goalPredicate.isPresent()) {
      return false;
    } else {
      return execution.getBrainslugContext().getPredicateEvaluator().evaluate(goalPredicate.get(), execution);
    }
  }

  protected void scheduleAsyncTask(AbstractTaskDefinition taskDefinition, ExecutionContext execution) {
    execution.getBrainslugContext().getAsyncTriggerScheduler()
      .schedule(
        new AsyncTrigger()
          .withNodeId(taskDefinition.getId())
          .withInstanceId(execution.getTrigger().getInstanceId())
          .withDefinitionId(execution.getTrigger().getDefinitionId())
      );
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
    } else if (delegateInstance instanceof Task){
      Method executeMethod = ReflectionUtil.getFirstMethodAnnotatedWith(delegateInstance.getClass(), Execute.class);
      invokeServiceMethodWithContext(context, delegateInstance, executeMethod);
    } else {
      throw new IllegalArgumentException(unsupportedDelegateMessage(delegateInstance));
    }
  }

  protected String unsupportedDelegateMessage(Object delegateInstance) {
    return "unsupported delegate class: " + delegateInstance.getClass()
    + " use " + Task.class.getName() + " or " + SimpleTask.class.getName();
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
