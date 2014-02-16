package brainslug.flow.execution.expression;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.PredicateEvaluator;
import brainslug.flow.execution.Execute;
import brainslug.flow.model.HandlerCallDefinition;
import brainslug.flow.model.ServiceCallDefinition;
import brainslug.flow.model.expression.Constant;
import brainslug.flow.model.expression.EqualDefinition;
import brainslug.flow.model.expression.Expression;
import brainslug.util.ReflectionUtil;

import java.lang.reflect.Method;

public class DefaultPredicateEvaluator implements PredicateEvaluator {

  BrainslugContext context;

  public DefaultPredicateEvaluator(BrainslugContext context) {
    this.context = context;
  }

  @Override
  public boolean evaluate(EqualDefinition predicate) {
    return getValue(predicate).equals(getExpectedValue(predicate));
  }

  private Object getExpectedValue(EqualDefinition predicate) {
    if (predicate.getExpectedValue() instanceof Constant) {
      return ((Constant) predicate.getExpectedValue()).getValue();
    }
    return predicate.getExpectedValue();
  }

  private Object getValue(EqualDefinition predicate) {
    if (predicate.getActual() instanceof Expression) {
      return ((Expression) predicate.getActual()).getValue();
    }
    if (predicate.getActual() instanceof Constant) {
      return ((Constant) predicate.getActual()).getValue();
    }
    if (predicate.getActual() instanceof ServiceCallDefinition) {
      return invokeServiceMethod((ServiceCallDefinition) predicate.getActual());
    }
    if (predicate.getActual() instanceof HandlerCallDefinition) {
      return invokeInlineMethod((HandlerCallDefinition) predicate.getActual());
    }
    throw new UnsupportedOperationException("unable to evaluate " + predicate);
  }

  private Object invokeServiceMethod(ServiceCallDefinition methodDefinition) {
    try {
      Method method = methodDefinition.getServiceClass().getMethod(methodDefinition.getMethodName());
      Object methodResult = method.invoke(context.getRegistry().getService(methodDefinition.getServiceClass()));
      return methodResult;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Object invokeInlineMethod(HandlerCallDefinition methodDefinition) {
    try {
      Method executeMethod = ReflectionUtil.getFirstMethodAnnotatedWith(methodDefinition.getCallee().getClass(), Execute.class);
      Object methodResult = executeMethod.invoke(methodDefinition.getCallee(), methodDefinition.getArguments().toArray());
      return methodResult;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
