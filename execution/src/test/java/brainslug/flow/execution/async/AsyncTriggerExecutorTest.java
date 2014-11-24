package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.TriggerContext;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AsyncTriggerExecutorTest {
  AsyncTriggerStore asyncTriggerStore = mock(AsyncTriggerStore.class);
  AbstractRetryStrategy retryStrategy = mock(AbstractRetryStrategy.class);

  BrainslugContext brainslugContext() {
    BrainslugContext context = mock(BrainslugContext.class);
    return context;
  }

  @Test
  public void shouldRemoveTriggerOnSuccessfulExecution() {
    // given:
    BrainslugContext context = brainslugContext();
    AsyncTrigger asyncTriggerWithRetriesLeft = spy(new AsyncTrigger());
    AsyncTriggerExecutor asyncTriggerExecutor = new AsyncTriggerExecutor();

    when(retryStrategy.nextRetry(anyLong(), any(Date.class))).thenReturn(new Date(0));
    when(asyncTriggerStore.removeTrigger(any(AsyncTrigger.class))).thenReturn(true);

    // when:
    asyncTriggerExecutor.execute(asyncTriggerWithRetriesLeft, retryStrategy, context, asyncTriggerStore);

    // then:
    InOrder order = inOrder(asyncTriggerWithRetriesLeft, asyncTriggerStore, retryStrategy);
    order.verify(asyncTriggerStore).removeTrigger(asyncTriggerWithRetriesLeft);
  }

  @Test
  public void shouldIncrementRetriesOnFailedExecution() {
    // given:
    BrainslugContext context = brainslugContext();
    AsyncTrigger asyncTriggerWithRetriesLeft = spy(new AsyncTrigger());
    AsyncTriggerExecutor asyncTriggerExecutor = new AsyncTriggerExecutor();

    doThrow(new RuntimeException()).when(context).trigger(any(TriggerContext.class));
    when(retryStrategy.nextRetry(anyLong(), any(Date.class))).thenReturn(new Date(0));

    // when:
    asyncTriggerExecutor.execute(asyncTriggerWithRetriesLeft, retryStrategy, context, asyncTriggerStore);

    // then:
    InOrder order = inOrder(asyncTriggerWithRetriesLeft, asyncTriggerStore, retryStrategy);
    order.verify(asyncTriggerWithRetriesLeft).incrementRetries();
    order.verify(retryStrategy).nextRetry(eq(1l), any(Date.class));
    order.verify(asyncTriggerStore).updateTrigger(asyncTriggerWithRetriesLeft);
  }
}