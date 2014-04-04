package brainslug.flow.execution.impl;

import brainslug.AbstractExecutionTest;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.listener.TriggerContext;
import org.junit.Test;

import static brainslug.util.IdUtil.id;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ExecutorServiceSchedulerTest extends AbstractExecutionTest {

  public static final String DEFINITION = "definition";
  public static final String TASK = "task";

  @Test
  public void shouldTriggerNodeExecution() {
    // given:
    BrainslugContext contextSpy = spy(context);
    ExecutorServiceScheduler executorServiceScheduler = new ExecutorServiceScheduler();
    executorServiceScheduler.setContext(contextSpy);

    // when:
    executorServiceScheduler.scheduleTask(id(DEFINITION), null, id(TASK));
    // then:
    verify(contextSpy).trigger(eq(new TriggerContext()
      .definitionId(id(DEFINITION))
      .nodeId(id(TASK))));
  }
}
