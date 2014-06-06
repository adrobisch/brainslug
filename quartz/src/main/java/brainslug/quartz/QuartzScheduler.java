package brainslug.quartz;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.Scheduler;
import brainslug.flow.execution.TriggerContext;
import brainslug.flow.model.Identifier;

import static brainslug.util.IdUtil.id;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

public class QuartzScheduler implements Scheduler {
  private static final String INSTANCE_ID = "instanceId";
  private static final String TASK_NODE_ID = "taskNodeId";
  private static final String DEFINITION_ID = "definitionId";
  private static final String SOURCE_NODE_ID = "sourceNodeId";

  private final org.quartz.Scheduler quartz;
  private BrainslugContext context;

  public QuartzScheduler(org.quartz.Scheduler quartz) {
    this.quartz = quartz;
    try {
      this.quartz.setJobFactory(new BrainslugContextJobFactory());
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }

  class BrainslugContextJobFactory implements JobFactory {
    @Override
    public Job newJob(TriggerFiredBundle bundle, org.quartz.Scheduler scheduler) throws SchedulerException {
      if(bundle.getJobDetail().getJobClass().isAssignableFrom(TaskJob.class)) {

        Identifier instanceId = id(bundle.getJobDetail().getJobDataMap().getString(INSTANCE_ID));
        Identifier taskNodeId = id(bundle.getJobDetail().getJobDataMap().getString(TASK_NODE_ID));
        Identifier definitionId = id(bundle.getJobDetail().getJobDataMap().getString(DEFINITION_ID));
        Identifier sourceNodeId = id(bundle.getJobDetail().getJobDataMap().getString(SOURCE_NODE_ID));

        return new TaskJob(context)
            .withTaskNodeId(taskNodeId)
            .withSourceNodeId(sourceNodeId)
            .withDefinitionId(definitionId)
            .withInstanceId(instanceId);
      }
      throw new IllegalArgumentException("can only handle task jobs");
    }
  }

  public static class TaskJob implements Job {
    BrainslugContext brainslugContext;
    Identifier taskNodeId;
    Identifier instanceId;
    Identifier definitionId;
    Identifier sourceNodeId;

    public TaskJob(BrainslugContext brainslugContext) {
      this.brainslugContext = brainslugContext;
    }

    TaskJob withTaskNodeId(Identifier taskNodeId) {
      this.taskNodeId = taskNodeId;
      return this;
    }

    TaskJob withInstanceId(Identifier instanceId) {
      this.instanceId = instanceId;
      return this;
    }

    TaskJob withDefinitionId(Identifier definitionId) {
      this.definitionId = definitionId;
      return this;
    }

    TaskJob withSourceNodeId(Identifier sourceNodeId) {
      this.sourceNodeId = sourceNodeId;
      return this;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      brainslugContext.trigger(new TriggerContext()
          .instanceId(instanceId)
          .nodeId(taskNodeId)
          .definitionId(definitionId));
    }
  }

  @Override
  public void scheduleTask(Identifier taskNodeId, Identifier sourceNodeId, Identifier instanceId, Identifier definitionId) {
    JobDetail job = newJob(TaskJob.class)
        .usingJobData(TASK_NODE_ID, taskNodeId.stringValue())
        .usingJobData(INSTANCE_ID, instanceId.stringValue())
        .usingJobData(DEFINITION_ID, definitionId.stringValue())
        .usingJobData(SOURCE_NODE_ID, sourceNodeId.stringValue())
        .storeDurably()
        .build();

    Trigger trigger = newTrigger()
        .startNow()
        .build();

    scheduleInQuartz(job, trigger);
  }

  private void scheduleInQuartz(JobDetail job, Trigger trigger) {
    try {
      quartz.scheduleJob(job, trigger);
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void start() {
    try {
      quartz.start();
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void stop() {
    try {
      quartz.shutdown(true);
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setContext(BrainslugContext context) {
    this.context = context;
  }
}
