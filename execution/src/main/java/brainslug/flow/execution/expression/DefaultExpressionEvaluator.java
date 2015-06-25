package brainslug.flow.execution.expression;

import brainslug.flow.execution.node.task.CallDefinitionExecutor;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.expression.EqualsExpression;
import brainslug.flow.expression.Expression;
import brainslug.flow.expression.Value;
import brainslug.flow.expression.Property;
import brainslug.flow.node.task.CallDefinition;

public class DefaultExpressionEvaluator implements ExpressionEvaluator {

  @Override
  public <T> T evaluate(Expression expression, ExecutionContext context, Class<T> expectedType) {
    Object expressionValue = evaluateExpression(expression, context);

    if (!expressionValue.getClass().isAssignableFrom(expectedType)) {
      throw new IllegalArgumentException("the expression " + expression + "did not evaluate to expected result type: " + expectedType);
    }

    return expectedType.cast(expressionValue);
  }

  protected Object evaluateExpression(Expression expression, ExecutionContext context) {
    if (expression instanceof EqualsExpression) {
      EqualsExpression equalsExpression = (EqualsExpression) expression;
      return getValue(equalsExpression.getLeft(), context).equals(getValue(equalsExpression.getRight(), context));
    } else {
      throw new IllegalArgumentException("unknown expression type " + expression.getClass());
    }
  }

  private Object propertyValue(Property<?> property, ExecutionContext context) {
    String propertyId = property.getValue().getId().toString();
    return context.getTrigger().getProperty(propertyId, Object.class);
  }

  private Object getValue(Expression expression, ExecutionContext context) {
    if (expression instanceof Property) {
      return propertyValue((Property) expression, context);
    } else if (expression instanceof Value) {
      return valueOf(((Value) expression).getValue(), context);
    } else {
      return evaluateExpression(expression ,context);
    }
  }

  private Object valueOf(Object value, ExecutionContext context) {
    if (value instanceof ContextPredicate) {
      return ((ContextPredicate) value).isFulfilled(context);
    }
    if (value instanceof PropertyPredicate) {
      return ((PropertyPredicate) value).isFulfilled(context.getTrigger().getProperties());
    }
    if (value instanceof CallDefinition) {
      return executeCall((CallDefinition) value, context);
    }
    if (value instanceof Value) {
      return valueOf(((Value) value).getValue(), context);
    }

    return value;
  }

  private Object executeCall(CallDefinition actual, ExecutionContext context) {
    return new CallDefinitionExecutor().execute(actual, context);
  }
}
