package brainslug.flow.listener;

public interface Listener {
  public void notify(TriggerContext<?> event);
}
