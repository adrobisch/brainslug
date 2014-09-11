package brainslug.flow.execution.impl;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.AsyncTaskScheduler;
import brainslug.flow.execution.TriggerContext;
import brainslug.flow.model.Identifier;

import java.util.concurrent.Executors;

public class ExecutorServiceScheduler implements AsyncTaskScheduler {
  protected BrainslugContext context;

  @Override
  public void scheduleTask(final Identifier taskNodeId, final Identifier sourceNodeId, final Identifier instanceId, final Identifier definitionId) {
    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        context.trigger(new TriggerContext()
            .instanceId(instanceId)
            .definitionId(definitionId)
            .nodeId(taskNodeId));
      }
    });
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
  }

  @Override
  public void setContext(BrainslugContext context) {
    this.context = context;
  }
}
