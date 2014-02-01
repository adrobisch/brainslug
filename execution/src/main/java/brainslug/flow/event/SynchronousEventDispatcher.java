package brainslug.flow.event;

import brainslug.flow.context.BrainslugContext;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

public class SynchronousEventDispatcher implements EventDispatcher {

  BrainslugContext context;
  Set<Subscriber> subscribers = Collections.synchronizedSet(new HashSet<Subscriber>());
  LinkedBlockingDeque<FlowEvent> queue = new LinkedBlockingDeque<FlowEvent>();

  @Override
  public void push(FlowEvent event) {
    queue.push(event);
  }

  @Override
  public void dispatch() {
    while (!queue.isEmpty()) {
      FlowEvent event = queue.pop();
      notifySubscribers(event);
    }
  }

  private void notifySubscribers(FlowEvent event) {
    for (Subscriber subscriber : subscribers) {
      subscriber.notify(event);
    }
  }

  @Override
  public void addSubscriber(Subscriber subscriber) {
    subscribers.add(subscriber);
  }

  @Override
  public void removeSubscriber(Subscriber subscriber) {
    subscribers.remove(subscriber);
  }

  @Override
  public void setContext(BrainslugContext context) {
    this.context = context;
  }
}
