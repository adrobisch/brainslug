package brainslug.flow.context;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.model.expression.EqualDefinition;

public interface PredicateEvaluator {
  public boolean evaluate(EqualDefinition predicate, ExecutionContext context);
}
