package brainslug.flow.event;

import brainslug.flow.context.BrainslugContext;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

public class SynchronousEventDispatcher implements EventDispatcher {

  BrainslugContext context;
  Map<EventPath, Set<Subscriber>> subscribers = new HashMap<EventPath, Set<Subscriber>>();
  LinkedBlockingDeque<QueuedEvent> queue = new LinkedBlockingDeque<QueuedEvent>();
  CountDownLatch countDownLatch;

  @Override
  public void push(EventPath path, FlowEvent event) {
    queue.push(new QueuedEvent(event, path));
  }

  @Override
  public void dispatch() {
    if (dispatchInProgress()) {
      return;
    }
    while (!queue.isEmpty()) {
      QueuedEvent event = queue.pop();
      notifySubscribers(event);
    }
    countDownLatch.countDown();
  }

  private boolean dispatchInProgress() {
    if (countDownLatch == null || countDownLatch.getCount() == 0) {
      countDownLatch = new CountDownLatch(1);
      return false;
    }
    return true;
  }

  private void notifySubscribers(QueuedEvent event) {
    System.out.println(event);

    if (subscribers.get(event.getEventPath()) == null) {
      throw new IllegalStateException("trying to notify empty set of subscribers");
    }

    for (Subscriber subscriber : subscribers.get(event.getEventPath())) {
      subscriber.notify(event.getFlowEvent());
    }
  }

  @Override
  public void addSubscriber(EventPath path, Subscriber subscriber) {
    getOrCreateSubscribers(path).add(subscriber);
  }

  Set<Subscriber> getOrCreateSubscribers(EventPath path) {
    if (subscribers.get(path) == null) {
      subscribers.put(path, Collections.synchronizedSet(new HashSet<Subscriber>()));
    }
    return subscribers.get(path);
  }

  @Override
  public void removeSubscriber(Subscriber subscriber) {
    for(Set<Subscriber> pathSubscribers : subscribers.values()) {
      if (pathSubscribers.remove(subscriber)) {
        break;
      }
    }
  }

  @Override
  public void setContext(BrainslugContext context) {
    this.context = context;
  }
}
