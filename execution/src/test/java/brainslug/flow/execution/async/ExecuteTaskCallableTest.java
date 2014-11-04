package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ExecuteTaskCallableTest {

  AsyncTriggerExecutor asyncTriggerExecutor = mock(AsyncTriggerExecutor.class);
  AsyncTriggerStore asyncTriggerStore = mock(AsyncTriggerStore.class);
  AbstractRetryStrategy retryStrategy = mock(AbstractRetryStrategy.class);

  BrainslugContext brainslugContext() {
    BrainslugContext context = mock(BrainslugContext.class);
    when(context.getAsyncTriggerStore()).thenReturn(asyncTriggerStore);
    return context;
  }

  @Test
  public void shouldIncrementRetriesOnFailedExecution() {
    // given:
    BrainslugContext context = brainslugContext();
    AsyncTrigger asyncTriggerWithRetriesLeft = taskMock(5);

    when(asyncTriggerExecutor.execute(asyncTriggerWithRetriesLeft, context))
      .thenReturn(new AsyncTriggerExecutionResult().setFailed(true).withException(new RuntimeException("error")));

    // when:
    new ExecuteTaskCallable(context, asyncTriggerWithRetriesLeft, asyncTriggerExecutor, retryStrategy).call();

    // then:
    InOrder order = inOrder(asyncTriggerWithRetriesLeft, asyncTriggerStore, retryStrategy);
    order.verify(asyncTriggerWithRetriesLeft).incrementRetries();
    order.verify(retryStrategy).nextRetry(eq(0l), any(Date.class));
    order.verify(asyncTriggerStore).storeTrigger(asyncTriggerWithRetriesLeft);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenNoRetriesLeft() {
    // given:
    BrainslugContext context = brainslugContext();
    AsyncTrigger asyncTriggerWithNoRetriesLeft = taskMock(0);

    // when:
    new ExecuteTaskCallable(context, asyncTriggerWithNoRetriesLeft, asyncTriggerExecutor, retryStrategy).call();
  }

  private AsyncTrigger taskMock(long retriesLeft) {
    AsyncTrigger asyncTrigger = mock(AsyncTrigger.class);
    when(asyncTrigger.getRetries()).thenReturn(5l - retriesLeft);
    when(asyncTrigger.getMaxRetries()).thenReturn(5l);

    when(asyncTrigger.incrementRetries())
      .thenReturn(asyncTrigger);

    when(asyncTrigger.withDueDate(anyLong()))
      .thenReturn(asyncTrigger);

    when(asyncTrigger.withErrorDetails(any(AsyncTriggerErrorDetails.class)))
      .thenReturn(asyncTrigger);

    when(retryStrategy.nextRetry(anyLong(), any(Date.class))).thenReturn(new Date(0));

    return asyncTrigger;
  }
}