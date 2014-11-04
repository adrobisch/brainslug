package brainslug.flow.execution.async;

import brainslug.AbstractExecutionTest;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.HashMapDefinitionStore;
import brainslug.flow.execution.TriggerContext;
import brainslug.flow.FlowBuilder;
import brainslug.flow.Identifier;
import brainslug.util.IdUtil;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static brainslug.util.IdUtil.id;
import static brainslug.util.TestId.ASYNCID;
import static com.jayway.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

public class ExecutorServiceSchedulerTest extends AbstractExecutionTest {

  public static final String TASK = "task";

  final BrainslugContext contextMockWithDefinition() {
    BrainslugContext context = mock(BrainslugContext.class);
    AsyncTriggerStore asyncTriggerStore = new ArrayListTriggerStore();

    when(context.getAsyncTriggerStore()).thenReturn(asyncTriggerStore);
    HashMapDefinitionStore definitionStore = new HashMapDefinitionStore();

    definitionStore.addDefinition(new FlowBuilder() {
      @Override
      public void define() {
        start(id("start")).execute(task(id(TASK)).async(true));
      }


      @Override
      public String getId() {
        return ASYNCID.name();
      }
    }.getDefinition());

    when(context.getDefinitionStore()).thenReturn(definitionStore);
    return context;
  }

  @Test
  public void shouldTriggerNodeExecution() {
    // given:
    BrainslugContext contextMock = contextMockWithDefinition();
    ExecutorServiceScheduler executorServiceScheduler = schedulerWithContext(contextMock);
    Identifier instanceId = IdUtil.id("instance");

    // when:
    executorServiceScheduler.schedule(new AsyncTrigger()
      .withNodeId(id(TASK))
      .withInstanceId(instanceId)
      .withDefinitionId(id(ASYNCID)));

    // then:
    TriggerContext expectedTrigger = new TriggerContext()
      .definitionId(id(ASYNCID))
      .instanceId(instanceId)
      .nodeId(id(TASK))
      .async(true)
      .signaling(true);

    await().until(contextWasTriggered(contextMock, expectedTrigger));
  }

  ExecutorServiceScheduler schedulerWithContext(BrainslugContext context) {
    ExecutorServiceScheduler executorServiceScheduler = new ExecutorServiceScheduler();
    executorServiceScheduler.setContext(context);

    executorServiceScheduler.start(
      new AsyncTriggerSchedulerOptions()
        .withScheduleDelay(500)
        .withSchedulePeriod(5000)
        .withScheduleUnit(TimeUnit.MILLISECONDS)
    );

    return executorServiceScheduler;
  }

  private Callable<Boolean> contextWasTriggered(final BrainslugContext contextMock, final TriggerContext expectedTrigger) {
    return new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        try {
          verify(contextMock, times(1)).trigger(eq(expectedTrigger));
          // verify will throw an assertion error until the mock was called
          return true;
        }catch (AssertionError e) {
          return false;
        }
      }
    };
  }
}
