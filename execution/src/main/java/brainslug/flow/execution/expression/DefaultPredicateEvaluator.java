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
    return getValue(predicate, context).equals(getExpectedValue(predicate));
  }

  private Object propertyValue(Property property, ExecutionContext context) {
    String propertyId = property.getValue().getId().toString();
    return context.getTrigger().getProperty(propertyId, Object.class);
  }

  private Object getExpectedValue(EqualDefinition predicate) {
    if (predicate.getExpectedValue() instanceof Expression) {
      return ((Expression) predicate.getExpectedValue()).getValue();
    }
    return predicate.getExpectedValue();
  }

  private Object getValue(EqualDefinition equalDefinition, ExecutionContext context) {
    if (equalDefinition.getActual() instanceof ContextPredicate) {
      return ((ContextPredicate) equalDefinition.getActual()).isFulfilled(context);
    }
    if (equalDefinition.getActual() instanceof PropertyPredicate) {
      return ((PropertyPredicate) equalDefinition.getActual()).isFulfilled(context.getTrigger().getProperties());
    }
    if (equalDefinition.getActual() instanceof ServicePredicate) {
      return ((ServicePredicate) equalDefinition.getActual()).isFulfilled(context.getBrainslugContext().getRegistry());
    }
    if (equalDefinition.getActual() instanceof Property) {
      return propertyValue((Property) equalDefinition.getActual(), context);
    }
    if (equalDefinition.getActual() instanceof Expression) {
      return ((Expression) equalDefinition.getActual()).getValue();
    }
    if (equalDefinition.getActual() instanceof ServiceCallDefinition) {
      return invokeServiceMethod((ServiceCallDefinition) equalDefinition.getActual(), context);
    }
    if (equalDefinition.getActual() instanceof HandlerCallDefinition) {
      return invokeHandlerMethod((HandlerCallDefinition) equalDefinition.getActual());
    }
    throw new UnsupportedOperationException("unable to evaluate " + equalDefinition);
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
