package brainslug.flow.event;

import static brainslug.flow.event.EventPathFactory.topic;

public interface EventPath {
  public static final EventPath TRIGGER_PATH = topic("trigger");
  public static final EventPath TOKENSTORE_PATH = topic("tokenstore");
}
