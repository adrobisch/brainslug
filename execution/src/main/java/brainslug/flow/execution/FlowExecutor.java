package brainslug.flow.execution;

import brainslug.flow.context.ContextAware;
import brainslug.flow.event.Subscriber;
import brainslug.flow.model.Identifier;

public interface FlowExecutor extends ContextAware, Subscriber {
  Identifier startFlow(Identifier definitionId, Identifier nodeId);
}
