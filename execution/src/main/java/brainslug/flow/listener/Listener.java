package brainslug.flow.listener;

import brainslug.flow.execution.TriggerContext;

public interface Listener {
  public void notify(TriggerContext<?> event);
}
