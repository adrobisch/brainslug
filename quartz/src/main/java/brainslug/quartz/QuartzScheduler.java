package brainslug.quartz;

import brainslug.flow.definition.Identifier;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.Trigger;
import brainslug.flow.execution.async.AbstractAsyncTriggerScheduler;
import brainslug.flow.execution.async.AsyncTrigger;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import static brainslug.util.IdUtil.id;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class QuartzScheduler extends AbstractAsyncTriggerScheduler {
  protected static final String INSTANCE_ID = "instanceId";
  protected static final String TASK_NODE_ID = "taskNodeId";
  protected static final String DEFINITION_ID = "definitionId";

  protected final org.quartz.Scheduler quartz;

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

        return new TaskJob(context)
            .withTaskNodeId(taskNodeId)
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

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      brainslugContext.trigger(new Trigger()
          .instanceId(instanceId)
          .nodeId(taskNodeId)
          .definitionId(definitionId)
          .async(true)
          .signaling(true)
      );
    }
  }

  @Override
  public void internalSchedule(AsyncTrigger asyncTrigger) {
    JobDetail job = newJob(TaskJob.class)
        .usingJobData(TASK_NODE_ID, asyncTrigger.getNodeId().stringValue())
        .usingJobData(INSTANCE_ID, asyncTrigger.getInstanceId().stringValue())
        .usingJobData(DEFINITION_ID, asyncTrigger.getDefinitionId().stringValue())
        .storeDurably()
        .build();

    org.quartz.Trigger trigger = newTrigger()
        .startNow()
        .build();

    scheduleInQuartz(job, trigger);
  }

  private void scheduleInQuartz(JobDetail job, org.quartz.Trigger trigger) {
    try {
      quartz.scheduleJob(job, trigger);
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void internalStart() {
    try {
      quartz.start();
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void internalStop() {
    try {
      quartz.shutdown(true);
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }
}
