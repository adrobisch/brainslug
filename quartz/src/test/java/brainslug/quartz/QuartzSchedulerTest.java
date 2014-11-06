package brainslug.quartz;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.SimpleTask;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.execution.async.AsyncTriggerSchedulerOptions;
import brainslug.flow.FlowBuilder;
import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import static com.jayway.awaitility.Awaitility.*;

import org.junit.Test;
import org.mockito.Mockito;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.concurrent.Callable;

import static brainslug.util.IdUtil.id;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class QuartzSchedulerTest {
  @Test
  public void shouldScheduleTask() throws Exception {
    // given:
    Scheduler scheduler = mock(Scheduler.class);

    QuartzScheduler quartzScheduler = quartzSchedulerWithContextMock(scheduler);

    Identifier taskId = id("task");
    Identifier instanceId = id("instance");
    Identifier definitionId = id("definition");

    // when:
    quartzScheduler.schedule(new AsyncTrigger()
      .withNodeId(taskId)
      .withInstanceId(instanceId)
      .withDefinitionId(definitionId));

    // then:
    verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
  }

  private QuartzScheduler quartzSchedulerWithContextMock(Scheduler scheduler) {
    QuartzScheduler quartzScheduler = new QuartzScheduler(scheduler);
    quartzScheduler.start(mock(BrainslugContext.class), new AsyncTriggerSchedulerOptions());
    return quartzScheduler;
  }

  @Test
  public void shouldExecuteAsyncTaskInFlowInstance() throws SchedulerException {
    // given:
    final SimpleAsyncTask simpleAsyncTask = Mockito.spy(new SimpleAsyncTask());

    FlowDefinition asyncTaskFlow = new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
            .execute(task(id("task"), simpleAsyncTask).async(true)).end(event(id("end")));
      }

    }.getDefinition();

    Scheduler quartzScheduler = createQuartzScheduler();

    BrainslugContext context = new BrainslugContext().withAsyncTriggerScheduler(new QuartzScheduler(quartzScheduler))
        .addFlowDefinition(asyncTaskFlow).start();
    // when:
    context.startFlow(asyncTaskFlow.getId(), id("start"));
    quartzScheduler.start();

    // then:
    await().until(taskCalled(simpleAsyncTask));
  }

  private Callable<Boolean> taskCalled(final SimpleAsyncTask task) {
    return new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return task.isCalled();
      }
    };
  }

  Scheduler createQuartzScheduler() {
    try {
      return StdSchedulerFactory.getDefaultScheduler();
    } catch (SchedulerException e) {
      throw new RuntimeException("could not create quartzscheduler");
    }
  }

  class SimpleAsyncTask extends SimpleTask {
    boolean called;

    @Override
    public void execute(ExecutionContext context) {
      called = true;
    }

    public boolean isCalled() {
      return called;
    }
  }
}
