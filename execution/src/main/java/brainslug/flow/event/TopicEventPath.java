package brainslug.flow.event;

public class TopicEventPath implements EventPath {
  String topic;

  public TopicEventPath(String topic) {
    this.topic = topic;
  }

  public String getTopic() {
    return topic;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TopicEventPath that = (TopicEventPath) o;

    if (topic != null ? !topic.equals(that.topic) : that.topic != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return topic != null ? topic.hashCode() : 0;
  }
}
