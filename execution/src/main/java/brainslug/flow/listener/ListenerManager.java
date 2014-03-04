package brainslug.flow.listener;

import brainslug.flow.context.ContextAware;

public interface ListenerManager extends ContextAware {
  public void notifyListeners(EventType type, TriggerContext context);
  public void addListener(EventType type, Listener listener);
  public void removeListener(Listener listener);
}
