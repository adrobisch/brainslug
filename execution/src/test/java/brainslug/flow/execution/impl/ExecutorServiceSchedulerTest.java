package brainslug.flow.execution.impl;

import brainslug.AbstractExecutionTest;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.TriggerContext;
import brainslug.flow.model.Identifier;
import org.junit.Test;

import java.util.concurrent.Callable;

import static brainslug.util.IdUtil.id;
import static com.jayway.awaitility.Awaitility.await;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExecutorServiceSchedulerTest extends AbstractExecutionTest {

  public static final String DEFINITION = "definition";
  public static final String TASK = "task";
  public static final String START = "start";

  @Test
  public void shouldTriggerNodeExecution() {
    // given:
    final BrainslugContext contextMock = mock(BrainslugContext.class);
    ExecutorServiceScheduler executorServiceScheduler = new ExecutorServiceScheduler();
    executorServiceScheduler.setContext(contextMock);

    final Identifier instanceId = id("instance");

    // when:
    executorServiceScheduler.scheduleTask(id(TASK), id(START), instanceId, id(DEFINITION));
    // then:
    TriggerContext expectedTrigger = new TriggerContext()
      .definitionId(id(DEFINITION))
      .instanceId(instanceId)
      .nodeId(id(TASK));

    await().until(contextWasTriggered(contextMock, expectedTrigger));
  }

  private Callable<Boolean> contextWasTriggered(final BrainslugContext contextMock, final TriggerContext expectedTrigger) {
    return new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        try {
          verify(contextMock).trigger(eq(expectedTrigger));
          // verify will throw an assertion error until the mock was called
          return true;
        }catch (AssertionError e) {
          return false;
        }
      }
    };
  }
}
