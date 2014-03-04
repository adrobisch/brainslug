package brainslug.flow.event;

public class EventPathFactory {
  public static EventPath path(String path) {
    return new TopicEventPath(path);
  }
}
