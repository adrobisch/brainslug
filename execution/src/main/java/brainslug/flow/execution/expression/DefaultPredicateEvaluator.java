package brainslug.flow.execution.expression;

import brainslug.flow.execution.Execute;
import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.node.task.HandlerCallDefinition;
import brainslug.flow.node.task.ServiceCallDefinition;
import brainslug.flow.expression.EqualDefinition;
import brainslug.flow.expression.Expression;
import brainslug.flow.expression.Property;
import brainslug.util.ReflectionUtil;

import java.lang.reflect.Method;

public class DefaultPredicateEvaluator implements PredicateEvaluator {

  @Override
  public boolean evaluate(EqualDefinition predicate, ExecutionContext context) {
    if (predicate.getActual() instanceof Property) {
      return evaluateProperty((Property) predicate.getActual(), getExpectedValue(predicate), context);
    }
    return getValue(predicate, context).equals(getExpectedValue(predicate));
  }

  private boolean evaluateProperty(Property property, Object expected, ExecutionContext context) {
    String propertyId = property.getValue().getId().toString();
    return context.getTrigger().getProperty(propertyId, Object.class).equals(expected);
  }

  private Object getExpectedValue(EqualDefinition predicate) {
    if (predicate.getExpectedValue() instanceof Expression) {
      return ((Expression) predicate.getExpectedValue()).getValue();
    }
    return predicate.getExpectedValue();
  }

  private Object getValue(EqualDefinition predicate, ExecutionContext context) {
    if (predicate.getActual() instanceof Expression) {
      return ((Expression) predicate.getActual()).getValue();
    }
    if (predicate.getActual() instanceof ServiceCallDefinition) {
      return invokeServiceMethod((ServiceCallDefinition) predicate.getActual(), context);
    }
    if (predicate.getActual() instanceof HandlerCallDefinition) {
      return invokeHandlerMethod((HandlerCallDefinition) predicate.getActual());
    }
    throw new UnsupportedOperationException("unable to evaluate " + predicate);
  }

  private Object invokeServiceMethod(ServiceCallDefinition methodDefinition, ExecutionContext context) {
    try {
      Method method = methodDefinition.getServiceClass().getMethod(methodDefinition.getMethodName());
      Object methodResult = method.invoke(context.getBrainslugContext().getRegistry().getService(methodDefinition.getServiceClass()));
      return methodResult;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Object invokeHandlerMethod(HandlerCallDefinition methodDefinition) {
    try {
      Method executeMethod = ReflectionUtil.getFirstMethodAnnotatedWith(methodDefinition.getCallee().getClass(), Execute.class);
      Object methodResult = executeMethod.invoke(methodDefinition.getCallee(), methodDefinition.getArguments().toArray());
      return methodResult;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
