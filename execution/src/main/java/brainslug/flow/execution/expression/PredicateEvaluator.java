package brainslug.flow.execution.expression;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.expression.EqualDefinition;

public interface PredicateEvaluator {
  public boolean evaluate(EqualDefinition predicate, ExecutionContext context);
}
