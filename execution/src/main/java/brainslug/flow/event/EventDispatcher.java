package brainslug.flow.event;

import brainslug.flow.context.ContextAware;

public interface EventDispatcher extends ContextAware {
  public void push(EventPath route, FlowEvent event);
  public void dispatch();

  public void addSubscriber(EventPath route, Subscriber subscriber);
  public void removeSubscriber(Subscriber subscriber);
}
