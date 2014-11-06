package brainslug.flow.node.event.timer;

import java.util.concurrent.TimeUnit;

public class StartTimerDefinition extends TimerDefinition {
  public StartTimerDefinition(long interval, TimeUnit unit) {
    super(interval, unit);
  }
}
