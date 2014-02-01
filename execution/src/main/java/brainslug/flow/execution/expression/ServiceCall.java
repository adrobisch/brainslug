package brainslug.flow.execution.expression;

import brainslug.flow.context.Registry;

public interface ServiceCall<T> {
  T call(Registry registry);
}
