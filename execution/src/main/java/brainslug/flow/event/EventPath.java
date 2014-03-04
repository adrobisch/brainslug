package brainslug.flow.event;

import static brainslug.flow.event.EventPathFactory.path;

public interface EventPath {
  public static final EventPath TRIGGER_PATH = path("trigger");
  public static final EventPath TOKENSTORE_PATH = path("tokenstore");
}
