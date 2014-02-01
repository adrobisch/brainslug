package brainslug.flow.event;

import brainslug.flow.context.ContextAware;

public interface EventDispatcher extends ContextAware {
  public void push(FlowEvent event);
  public void dispatch();

  public void addSubscriber(Subscriber subscriber);
  public void removeSubscriber(Subscriber subscriber);
}
