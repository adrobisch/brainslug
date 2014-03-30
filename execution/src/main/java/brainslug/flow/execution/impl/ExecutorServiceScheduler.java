package brainslug.flow.execution.impl;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.Scheduler;
import brainslug.flow.listener.TriggerContext;
import brainslug.flow.model.Identifier;

import java.util.concurrent.Executors;

public class ExecutorServiceScheduler implements Scheduler {
  protected BrainslugContext context;

  @Override
  public void scheduleTask(final Identifier definitionId, final Identifier taskNodeId) {
    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        context.trigger(new TriggerContext()
          .definitionId(definitionId)
          .nodeId(taskNodeId));
      }
    });
  }

  @Override
  public void setContext(BrainslugContext context) {
    this.context = context;
  }
}
