package brainslug.flow.listener;

import brainslug.flow.execution.TriggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DefaultListenerManager implements ListenerManager {

  Logger log = LoggerFactory.getLogger(DefaultListenerManager.class);

  Map<EventType, Set<Listener>> listeners = Collections.synchronizedMap(new HashMap<EventType, Set<Listener>>());

  synchronized public void notifyListeners(EventType type, TriggerContext context) {
    log.debug("notify listeners [{}]: {}, ", type, context);

    if (listeners.get(type) == null) {
      log.debug("no listeners in context {} with event type {}", context, type);
      return;
    }

    for (Listener listener : listeners.get(type)) {
      listener.notify(context);
    }
  }

  @Override
  public void addListener(EventType type, Listener listener) {
    getOrCreateListener(type).add(listener);
  }

  Set<Listener> getOrCreateListener(EventType type) {
    if (listeners.get(type) == null) {
      listeners.put(type, Collections.synchronizedSet(new HashSet<Listener>()));
    }
    return listeners.get(type);
  }

  @Override
  public void removeListener(Listener listener) {
    for(Set<Listener> listeners : this.listeners.values()) {
      if (listeners.remove(listener)) {
        break;
      }
    }
  }
}
