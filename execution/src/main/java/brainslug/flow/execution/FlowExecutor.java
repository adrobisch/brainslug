package brainslug.flow.execution;

import brainslug.flow.context.ContextAware;
import brainslug.flow.listener.TriggerContext;
import brainslug.flow.model.Identifier;

public interface FlowExecutor extends ContextAware {
  Identifier startFlow(Identifier definitionId, Identifier nodeId);
  void trigger(TriggerContext triggerContext);
}
