package brainslug.flow.execution.expression;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.expression.Expression;

public interface ExpressionEvaluator {
  <T> T evaluate(Expression expression, ExecutionContext context, Class<T> resultType);
}
