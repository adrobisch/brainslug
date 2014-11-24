package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.DefaultBrainslugContext;
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
    return context;
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenNoRetriesLeft() {
    // given:
    BrainslugContext context = brainslugContext();
    AsyncTrigger asyncTriggerWithNoRetriesLeft = taskMock(0);

    // when:
    new ExecuteTaskCallable(context, asyncTriggerWithNoRetriesLeft, asyncTriggerStore, asyncTriggerExecutor, retryStrategy).call();
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