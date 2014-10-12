package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractAsyncTaskScheduler implements AsyncTaskScheduler {
  protected BrainslugContext context;
  protected AtomicBoolean running = new AtomicBoolean(false);
  protected AsyncTaskSchedulerOptions options;

  @Override
  public void scheduleTask(AsyncTask asyncTask) {
    if (running.get()) {
      internalScheduleTask(asyncTask);
    }
  }

  protected void internalScheduleTask(AsyncTask asyncTask) {
    context.getAsyncTaskStore().storeTask(asyncTask);
  }

  @Override
  public synchronized void start(AsyncTaskSchedulerOptions options) {
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
