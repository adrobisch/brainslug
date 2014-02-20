package brainslug.flow.event;

public class EventPathFactory {
  public static EventPath topic(String topic) {
    return new TopicEventPath(topic);
  }
}
