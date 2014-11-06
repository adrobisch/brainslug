package brainslug.flow.node.event;

import brainslug.flow.node.event.timer.StartTimerDefinition;
import brainslug.util.Option;

public class StartEvent {
  StartTimerDefinition startTimerDefinition;

  public Option<StartTimerDefinition> getStartTimerDefinition() {
    return Option.of(startTimerDefinition);
  }

  public StartEvent withRecurringTimerDefinition(StartTimerDefinition startTimerDefinition) {
    this.startTimerDefinition = startTimerDefinition;
    return this;
  }
}
