package brainslug.flow.execution.expression;

import brainslug.flow.execution.node.task.CallDefinitionExecutor;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.expression.EqualDefinition;
import brainslug.flow.expression.Expression;
import brainslug.flow.expression.Property;
import brainslug.flow.node.task.CallDefinition;

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
    if (equalDefinition.getActual() instanceof Property) {
      return propertyValue((Property) equalDefinition.getActual(), context);
    }
    if (equalDefinition.getActual() instanceof Expression) {
      return ((Expression) equalDefinition.getActual()).getValue();
    }
    if (equalDefinition.getActual() instanceof CallDefinition) {
      return executeCall((CallDefinition) equalDefinition.getActual(), context);
    }
    throw new UnsupportedOperationException("unable to evaluate " + equalDefinition);
  }

  private Object executeCall(CallDefinition actual, ExecutionContext context) {
    return new CallDefinitionExecutor().execute(actual, context);
  }
}
