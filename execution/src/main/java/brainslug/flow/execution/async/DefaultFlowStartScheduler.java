package brainslug.flow.execution.async;

import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.node.event.StartEvent;
import brainslug.flow.node.event.timer.StartTimerDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DefaultFlowStartScheduler implements AsyncFlowStartScheduler {
  private BrainslugContext context;
  private Set<TimedFlowDefinition> timedDefinitions;
  private Map<Identifier, Long> lastStart = new ConcurrentHashMap<Identifier, Long>();

  ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
  SchedulerOptions schedulerOptions;

  @Override
  public void start(SchedulerOptions schedulerOptions, BrainslugContext brainslugContext, Collection<FlowDefinition> definitions) {
    this.schedulerOptions = schedulerOptions;
    this.context = brainslugContext;

    this.timedDefinitions = getFlowDefinitionsWithStartTimer(definitions);

    startScheduler();
  }

  private void startScheduler() {
    scheduledExecutorService.scheduleAtFixedRate(new StartDueDefinitionsRunnable(),
      schedulerOptions.getScheduleDelay(),
      schedulerOptions.getSchedulePeriod(),
      schedulerOptions.getScheduleUnit()
    );
  }

  @Override
  public void stop() {
  }

  public Set<TimedFlowDefinition> getFlowDefinitionsWithStartTimer(Collection<FlowDefinition> definitions) {
    Set<TimedFlowDefinition> definitionsWithTimer = new HashSet<TimedFlowDefinition>();
    for (FlowDefinition definition : definitions) {
      for (FlowNodeDefinition<?> node : definition.getNodes()) {
        if (node.is(StartEvent.class) &&
          node.as(StartEvent.class).getStartTimerDefinition().isPresent()) {

          definitionsWithTimer.add(new TimedFlowDefinition(definition, node.as(StartEvent.class)
            .getStartTimerDefinition().get(), node));
        }
      }
    }
    return definitionsWithTimer;
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
          if (isDue(definition)) {
            context.startFlow(definition.getFlowDefinition().getId(),
              definition.getStartNode().getId());

            lastStart.put(definition.getFlowDefinition().getId(), new Date().getTime());
          }
        }
      } catch (Exception e) {
        log.error("error during starting timed definitions", e);
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

  public DefaultFlowStartScheduler withScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
    this.scheduledExecutorService = scheduledExecutorService;
    return this;
  }

  public SchedulerOptions getSchedulerOptions() {
    return schedulerOptions;
  }

  public DefaultFlowStartScheduler withSchedulerOptions(SchedulerOptions schedulerOptions) {
    this.schedulerOptions = schedulerOptions;
    return this;
  }
}
