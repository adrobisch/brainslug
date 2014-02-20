package brainslug.flow.event;

public class QueuedEvent {
  FlowEvent flowEvent;
  EventPath eventPath;

  public QueuedEvent(FlowEvent flowEvent, EventPath eventPath) {
    this.flowEvent = flowEvent;
    this.eventPath = eventPath;
  }

  public FlowEvent getFlowEvent() {
    return flowEvent;
  }

  public EventPath getEventPath() {
    return eventPath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    QueuedEvent that = (QueuedEvent) o;

    if (eventPath != null ? !eventPath.equals(that.eventPath) : that.eventPath != null) return false;
    if (flowEvent != null ? !flowEvent.equals(that.flowEvent) : that.flowEvent != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = flowEvent != null ? flowEvent.hashCode() : 0;
    result = 31 * result + (eventPath != null ? eventPath.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "QueuedEvent{" +
      "flowEvent=" + flowEvent +
      ", eventPath=" + eventPath +
      '}';
  }
}
