package brainslug.quartz;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.SimpleTask;
import brainslug.flow.model.FlowBuilder;
import brainslug.flow.model.FlowDefinition;
import brainslug.flow.model.Identifier;
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

    QuartzScheduler quartzScheduler = new QuartzScheduler(scheduler);

    Identifier taskId = id("task");
    Identifier instanceId = id("instance");
    Identifier definitionId = id("definition");
    Identifier sourceNodeId = id("start");

    // when:
    quartzScheduler.scheduleTask(taskId, sourceNodeId, instanceId, definitionId);
    // then:
    verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
  }

  @Test
  public void shouldExecuteAsyncTaskInFlowInstance() throws SchedulerException {
    // GIVEN:
    final AsyncTask asyncTask = Mockito.spy(new AsyncTask());

    FlowDefinition asyncTaskFlow = new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
            .execute(task(id("task"), asyncTask).async(true)).end(event(id("end")));
      }

    }.getDefinition();

    Scheduler quartzScheduler = createQuartzScheduler();

    BrainslugContext context = new BrainslugContext().withScheduler(new QuartzScheduler(quartzScheduler))
        .addFlowDefinition(asyncTaskFlow);
    // when:
    Identifier instanceId = context.startFlow(asyncTaskFlow.getId(), id("start"));
    quartzScheduler.start();

    // then:
    await().until(taskCalled(asyncTask));
  }

  private Callable<Boolean> taskCalled(final AsyncTask task) {
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

  class AsyncTask extends SimpleTask {
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
