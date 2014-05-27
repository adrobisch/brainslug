package brainslug.flow.execution.impl;

import brainslug.AbstractExecutionTest;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.listener.TriggerContext;
import brainslug.flow.model.Identifier;
import org.junit.Test;

import static brainslug.util.IdUtil.id;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ExecutorServiceSchedulerTest extends AbstractExecutionTest {

  public static final String DEFINITION = "definition";
  public static final String TASK = "task";
  public static final String START = "task";

  @Test
  public void shouldTriggerNodeExecution() {
    // given:
    BrainslugContext contextSpy = spy(context);
    ExecutorServiceScheduler executorServiceScheduler = new ExecutorServiceScheduler();
    executorServiceScheduler.setContext(contextSpy);

    Identifier instanceId = id("instance");

    // when:
    executorServiceScheduler.scheduleTask(id(TASK), id(START), instanceId, id(DEFINITION));
    // then:
    verify(contextSpy).trigger(eq(new TriggerContext()
        .definitionId(id(DEFINITION))
        .instanceId(instanceId)
        .nodeId(id(TASK))));
  }
}
