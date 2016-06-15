package brainslug.flow.execution.async;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.definition.Identifier;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.node.event.StartEvent;
import brainslug.flow.node.event.timer.StartTimerDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class ExecutorServiceFlowStartScheduler implements AsyncFlowStartScheduler {
  private  BrainslugContext context;
  private List<TimedFlowDefinition> timedDefinitions = new CopyOnWriteArrayList<TimedFlowDefinition>();
  private Map<Identifier, Long> lastStart = new ConcurrentHashMap<Identifier, Long>();

  ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
  SchedulerOptions schedulerOptions;
  ScheduledFuture<?> scheduledFuture;

  @Override
  public synchronized void start(SchedulerOptions schedulerOptions, BrainslugContext brainslugContext, Collection<FlowDefinition> definitions) {
    this.schedulerOptions = schedulerOptions;
    this.context = brainslugContext;

    addFlowDefinitionsWithStartTimer(definitions);

    startScheduler();
  }

  private void startScheduler() {
    if (schedulerOptions.isDisabled()) {
      return;
    }

    scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new StartDueDefinitionsRunnable(),
      schedulerOptions.getScheduleDelay(),
      schedulerOptions.getSchedulePeriod(),
      schedulerOptions.getScheduleUnit()
    );
  }

  @Override
  public synchronized void stop() {
    if (scheduledFuture !=  null && !scheduledFuture.isDone()) {
      scheduledFuture.cancel(false);
    }
  }

  public List<TimedFlowDefinition> addFlowDefinitionsWithStartTimer(Collection<FlowDefinition> definitions) {
    for (FlowDefinition definition : definitions) {
      for (FlowNodeDefinition<?> node : definition.getNodes()) {
        if (node.is(StartEvent.class) &&
          node.as(StartEvent.class).getStartTimerDefinition().isPresent()) {

          timedDefinitions.add(new TimedFlowDefinition(definition, node.as(StartEvent.class)
            .getStartTimerDefinition().get(), node));
        }
      }
    }
    return timedDefinitions;
  }

  class TimedFlowDefinition {
    FlowDefinition flowDefinition;
    StartTimerDefinition startTimerDefinition;
    FlowNodeDefinition startNode;

    TimedFlowDefinition(FlowDefinition flowDefinition, StartTimerDefinition startTimerDefinition, FlowNodeDefinition startNode) {
      this.flowDefinition = flowDefinition;
      this.startTimerDefinition = startTimerDefinition;
      this.startNode = startNode;
    }

    public FlowDefinition getFlowDefinition() {
      return flowDefinition;
    }

    public StartTimerDefinition getStartTimerDefinition() {
      return startTimerDefinition;
    }

    public FlowNodeDefinition getStartNode() {
      return startNode;
    }
  }

  class StartDueDefinitionsRunnable implements Runnable {

    Logger log = LoggerFactory.getLogger(StartDueDefinitionsRunnable.class);

    @Override
    public void run() {
      log.debug("checking for due flows...");
      try {
        for (TimedFlowDefinition definition : timedDefinitions) {
          startFlowIfDue(definition);
        }
      } catch (Exception timedStartException) {
        log.error("error during while starting timed definitions", timedStartException);
      }
    }

    private void startFlowIfDue(TimedFlowDefinition definition) {
      try {
        if (isDue(definition)) {
          context.startFlow(definition.getFlowDefinition().getId(),
            definition.getStartNode().getId());

          lastStart.put(definition.getFlowDefinition().getId(), new Date().getTime());
        }
      } catch (Exception exceptionDuringStart) {
        log.error("error during start of " + definition.getFlowDefinition().getName(), exceptionDuringStart);
      }
    }

    boolean isDue(TimedFlowDefinition definition) {
      Long lastStartTime = lastStart.get(definition.getFlowDefinition().getId());
      if (lastStartTime == null) {
        return true;
      } else {
        StartTimerDefinition timerDefinition = definition.getStartTimerDefinition();
        return new Date().getTime() > lastStartTime + timerDefinition.getUnit().toMillis(timerDefinition.getDuration());
      }
    }
  }

  public ScheduledExecutorService getScheduledExecutorService() {
    return scheduledExecutorService;
  }

  public ExecutorServiceFlowStartScheduler withScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
    this.scheduledExecutorService = scheduledExecutorService;
    return this;
  }

  public SchedulerOptions getSchedulerOptions() {
    return schedulerOptions;
  }

  public ExecutorServiceFlowStartScheduler withSchedulerOptions(SchedulerOptions schedulerOptions) {
    this.schedulerOptions = schedulerOptions;
    return this;
  }

  public List<TimedFlowDefinition> getTimedDefinitions() {
    return timedDefinitions;
  }
}
