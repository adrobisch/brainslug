package brainslug.flow.listener;

import brainslug.flow.context.TriggerContext;

public interface Listener {
  public void notify(TriggerContext event);
}
