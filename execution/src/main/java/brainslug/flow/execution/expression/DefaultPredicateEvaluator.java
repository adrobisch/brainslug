package brainslug.flow.execution.expression;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.PredicateEvaluator;
import brainslug.flow.model.MethodCallDefinition;
import brainslug.flow.model.expression.Constant;
import brainslug.flow.model.expression.EqualDefinition;
import brainslug.flow.model.expression.Expression;

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
      Object expressionValue = ((Expression) predicate.getActual()).getValue();
      if(expressionValue instanceof ServiceCall) {
        return ((ServiceCall) expressionValue).call(context.getRegistry());
      }
    }
    if (predicate.getActual() instanceof Constant) {
      return ((Constant) predicate.getActual()).getValue();
    }
    if (predicate.getActual() instanceof MethodCallDefinition) {
      return invokeMethod((MethodCallDefinition) predicate.getActual());
    }
    throw new UnsupportedOperationException("unable to evaluate " + predicate);
  }

  private Object invokeMethod(MethodCallDefinition methodDefinition) {
    try {
      Method method = methodDefinition.getServiceClass().getMethod(methodDefinition.getMethodName());
      Object methodResult = method.invoke(context.getRegistry().getService(methodDefinition.getServiceClass()));
      return methodResult;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
