package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractAsyncTriggerScheduler implements AsyncTriggerScheduler {
  protected BrainslugContext context;
  protected AtomicBoolean running = new AtomicBoolean(false);
  protected AsyncTriggerSchedulerOptions options;

  @Override
  public void schedule(AsyncTrigger asyncTrigger) {
    if (running.get()) {
      internalSchedule(asyncTrigger);
    }
  }

  protected void internalSchedule(AsyncTrigger asyncTrigger) {
    context.getAsyncTriggerStore().storeTrigger(asyncTrigger);
  }

  @Override
  public synchronized void start(AsyncTriggerSchedulerOptions options) {
    this.options = options;

    if (this.context == null) {
      throw new IllegalStateException("context must be set to start async task scheduler");
    }

    internalStart();

    running.set(true);
  }

  protected void internalStart() {}

  @Override
  public synchronized void stop() {
    internalStop();

    running.set(false);
  }

  protected void internalStop() {
  }

  @Override
  public void setContext(BrainslugContext context) {
    this.context = context;
  }
}
