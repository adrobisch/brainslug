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

  AsyncTaskExecutor asyncTaskExecutor = mock(AsyncTaskExecutor.class);
  AsyncTaskStore asyncTaskStore = mock(AsyncTaskStore.class);
  AbstractRetryStrategy retryStrategy = mock(AbstractRetryStrategy.class);

  BrainslugContext brainslugContext() {
    BrainslugContext context = mock(BrainslugContext.class);
    when(context.getAsyncTaskStore()).thenReturn(asyncTaskStore);
    return context;
  }

  @Test
  public void shouldIncrementRetriesOnFailedExecution() {
    // given:
    BrainslugContext context = brainslugContext();
    AsyncTask asyncTaskWithRetriesLeft = taskMock(5);

    when(asyncTaskExecutor.execute(asyncTaskWithRetriesLeft, context))
      .thenReturn(new AsyncTaskExecutionResult().setFailed(true).withException(new RuntimeException("error")));

    // when:
    new ExecuteTaskCallable(context, asyncTaskWithRetriesLeft, asyncTaskExecutor, retryStrategy).call();

    // then:
    InOrder order = inOrder(asyncTaskWithRetriesLeft, asyncTaskStore, retryStrategy);
    order.verify(asyncTaskWithRetriesLeft).incrementRetries();
    order.verify(retryStrategy).nextRetry(eq(0l), any(Date.class));
    order.verify(asyncTaskStore).storeTask(asyncTaskWithRetriesLeft);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenNoRetriesLeft() {
    // given:
    BrainslugContext context = brainslugContext();
    AsyncTask asyncTaskWithNoRetriesLeft = taskMock(0);

    // when:
    new ExecuteTaskCallable(context, asyncTaskWithNoRetriesLeft, asyncTaskExecutor, retryStrategy).call();
  }

  private AsyncTask taskMock(long retriesLeft) {
    AsyncTask asyncTask = mock(AsyncTask.class);
    when(asyncTask.getRetries()).thenReturn(5l - retriesLeft);
    when(asyncTask.getMaxRetries()).thenReturn(5l);

    when(asyncTask.incrementRetries())
      .thenReturn(asyncTask);

    when(asyncTask.withDueDate(anyLong()))
      .thenReturn(asyncTask);

    when(asyncTask.withErrorDetails(any(AsyncTaskErrorDetails.class)))
      .thenReturn(asyncTask);

    when(retryStrategy.nextRetry(anyLong(), any(Date.class))).thenReturn(new Date(0));

    return asyncTask;
  }
}