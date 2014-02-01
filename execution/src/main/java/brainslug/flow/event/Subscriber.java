package brainslug.flow.event;

public interface Subscriber {
  public void notify(FlowEvent event);
}
