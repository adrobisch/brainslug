package brainslug.quartz;

import brainslug.flow.model.Identifier;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

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

    // when:
    quartzScheduler.scheduleTask(taskId, instanceId, definitionId);
    // then:
    verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
  }
}
