package brainslug.flow.execution.async;

import brainslug.AbstractExecutionTest;
import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.DefaultBrainslugContext;
import brainslug.flow.context.Trigger;
import brainslug.flow.context.TriggerContext;
import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.definition.Identifier;
import brainslug.util.IdUtil;
import org.junit.Test;
import org.mockito.verification.VerificationMode;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static brainslug.util.IdUtil.id;
import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ExecutorServiceAsyncTriggerSchedulerTest extends AbstractExecutionTest {

  public static final String START = "start";
  public static final String TASK = "task";
  public static final String EVENT = "event";
  public static final String ASYNC_TASK_FLOW = "taskFlow";
  public static final String WAIT_EVENT_FLOW = "waitFlow";

  AsyncTriggerStore asyncTriggerStore = spy(new ArrayListTriggerStore());

  final BrainslugContext contextMockWithDefinitions() {
    BrainslugContext context = mock(DefaultBrainslugContext.class);

    FlowDefinition taskFlow = taskFlow();
    FlowDefinition waitEventFlow = waitEventFlow();

    when(context.getDefinitionById(taskFlow.getId())).thenReturn(taskFlow);
    when(context.getDefinitionById(waitEventFlow.getId())).thenReturn(waitEventFlow);

    return context;
  }

  private FlowDefinition taskFlow() {
    return new FlowBuilder() {
      @Override
      public void define() {
        flowId(id(ASYNC_TASK_FLOW));

        start(id(START)).execute(task(id(TASK)).async(true));
      }

    }.getDefinition();
  }

  private FlowDefinition waitEventFlow() {
    return new FlowBuilder() {
      @Override
      public void define() {
        flowId(id(WAIT_EVENT_FLOW));

        start(id(START)).waitFor(event(id(EVENT)));
      }

    }.getDefinition();
  }

  @Test
  public void shouldNotScheduleTriggerIfNotRunning() {
    new ExecutorServiceAsyncTriggerScheduler() {
      @Override
      protected void internalSchedule(AsyncTrigger asyncTrigger) {
        throw new AssertionError("should not happen");
      }
    }.schedule(new AsyncTrigger());
  }

  @Test
  public void shouldStartIfOptionIsDisabled() {
    ExecutorServiceAsyncTriggerScheduler executorServiceAsyncTriggerScheduler = new ExecutorServiceAsyncTriggerScheduler();

    executorServiceAsyncTriggerScheduler
      .start(
        mock(BrainslugContext.class),
        asyncTriggerStore,
        new AsyncTriggerSchedulerOptions().setDisabled(true)
      );

    assertThat(executorServiceAsyncTriggerScheduler.isRunning()).isFalse();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionOnNullContext() {
    ExecutorServiceAsyncTriggerScheduler executorServiceAsyncTriggerScheduler = new ExecutorServiceAsyncTriggerScheduler();

    executorServiceAsyncTriggerScheduler
      .start(
        null,
        asyncTriggerStore,
        new AsyncTriggerSchedulerOptions()
      );
  }

  @Test
  public void shouldCallHooksOnStartAndStop() {
    final AtomicBoolean startHookCalled = new AtomicBoolean();
    final AtomicBoolean stopHookCalled = new AtomicBoolean();

    ExecutorServiceAsyncTriggerScheduler executorServiceAsyncTriggerScheduler = new ExecutorServiceAsyncTriggerScheduler() {
      @Override
      protected void internalStart() {
        startHookCalled.set(true);
      }

      @Override
      protected void internalStop() {
        stopHookCalled.set(true);
      }
    };

    executorServiceAsyncTriggerScheduler
      .start(
        mock(BrainslugContext.class),
        asyncTriggerStore,
        new AsyncTriggerSchedulerOptions()
      );

    executorServiceAsyncTriggerScheduler.stop();

    assertThat(startHookCalled.get()).isTrue();
    assertThat(stopHookCalled.get()).isTrue();
    assertThat(executorServiceAsyncTriggerScheduler.isRunning()).isFalse();
  }

  @Test
  public void shouldTriggerTaskNodeExecution() {
    // given:
    BrainslugContext contextMock = contextMockWithDefinitions();
    ExecutorServiceAsyncTriggerScheduler executorServiceScheduler = schedulerWithContext(contextMock);
    Identifier instanceId = IdUtil.id("instance");

    // when:
    executorServiceScheduler.schedule(new AsyncTrigger()
      .withNodeId(id(TASK))
      .withInstanceId(instanceId)
      .withDefinitionId(id(ASYNC_TASK_FLOW)));

    // then:
    TriggerContext expectedTrigger = new Trigger()
      .definitionId(id(ASYNC_TASK_FLOW))
      .instanceId(instanceId)
      .nodeId(id(TASK))
      .async(true)
      .signaling(true);

    await().until(contextWasTriggered(contextMock, expectedTrigger, times(1)));
  }

  @Test
  public void shouldScheduleExecutionMoreThanOnce() {
    // given:
    BrainslugContext contextMock = contextMockWithDefinitions();
    ExecutorServiceAsyncTriggerScheduler executorServiceScheduler = schedulerWithContext(contextMock);
    Identifier instanceId = IdUtil.id("instance");

    // when:
    executorServiceScheduler.schedule(new AsyncTrigger()
      .withId(id("trigger1"))
      .withNodeId(id(TASK))
      .withInstanceId(instanceId)
      .withDefinitionId(id(ASYNC_TASK_FLOW)));

    executorServiceScheduler.schedule(new AsyncTrigger()
      .withId(id("trigger2"))
      .withNodeId(id(TASK))
      .withInstanceId(instanceId)
      .withDefinitionId(id(ASYNC_TASK_FLOW)));

    // then:
    TriggerContext expectedTrigger = new Trigger()
      .definitionId(id(ASYNC_TASK_FLOW))
      .instanceId(instanceId)
      .nodeId(id(TASK))
      .async(true)
      .signaling(true);

    await().until(contextWasTriggered(contextMock, expectedTrigger, times(2)));
  }

  @Test
  public void shouldTriggerEventNodeExecution() {
    // given:
    BrainslugContext contextMock = contextMockWithDefinitions();
    ExecutorServiceAsyncTriggerScheduler executorServiceScheduler = schedulerWithContext(contextMock);
    Identifier instanceId = IdUtil.id("instance");

    // when:
    executorServiceScheduler.schedule(new AsyncTrigger()
      .withNodeId(id(EVENT))
      .withInstanceId(instanceId)
      .withDefinitionId(id(WAIT_EVENT_FLOW)));

    // then:
    TriggerContext expectedTrigger = new Trigger()
      .definitionId(id(WAIT_EVENT_FLOW))
      .instanceId(instanceId)
      .nodeId(id(EVENT))
      .async(true)
      .signaling(true);

    await().until(contextWasTriggered(contextMock, expectedTrigger, times(1)));
  }

  ExecutorServiceAsyncTriggerScheduler schedulerWithContext(BrainslugContext context) {
    ExecutorServiceAsyncTriggerScheduler executorServiceScheduler = new ExecutorServiceAsyncTriggerScheduler();

    executorServiceScheduler.start(context,
      asyncTriggerStore,
      new AsyncTriggerSchedulerOptions()
        .withMaxTaskCount(1)
        .withScheduleDelay(500)
        .withSchedulePeriod(500)
        .withScheduleUnit(TimeUnit.MILLISECONDS)
    );

    return executorServiceScheduler;
  }

  private Callable<Boolean> contextWasTriggered(final BrainslugContext contextMock, final TriggerContext expectedTrigger, final VerificationMode verificationMode) {
    return new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        try {
          verify(contextMock, verificationMode).trigger(eq(expectedTrigger));
          // verify will throw an assertion error until the mock was called
          return true;
        } catch (AssertionError e) {
          return false;
        }
      }
    };
  }
}
