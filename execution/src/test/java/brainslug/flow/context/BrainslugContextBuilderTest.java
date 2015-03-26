package brainslug.flow.context;

import brainslug.flow.execution.FlowExecutor;
import brainslug.flow.execution.async.AsyncFlowStartScheduler;
import brainslug.flow.execution.async.AsyncTriggerScheduler;
import brainslug.flow.execution.async.AsyncTriggerSchedulerOptions;
import brainslug.flow.execution.async.AsyncTriggerStore;
import brainslug.flow.execution.token.TokenStore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


public class BrainslugContextBuilderTest {
  @Test
  public void shouldCreateContextWithDefaults() {
    DefaultBrainslugContext createdContext = new BrainslugContextBuilder().build();

    assertThat(createdContext.getAsyncTriggerScheduler()).isNotNull();
    assertThat(createdContext.getTokenStore()).isNotNull();
    assertThat(createdContext.getFlowExecutor()).isNotNull();
  }

  @Test
  public void shouldCreateContextFromValues() {
    AsyncTriggerScheduler asyncTriggerScheduler = mock(AsyncTriggerScheduler.class);
    TokenStore tokenStore = mock(TokenStore.class);
    FlowExecutor flowExecutor = mock(FlowExecutor.class);
    AsyncTriggerSchedulerOptions asyncSchedulerOptions = mock(AsyncTriggerSchedulerOptions.class);
    AsyncFlowStartScheduler flowStartScheduler = mock(AsyncFlowStartScheduler.class);
    AsyncTriggerStore asyncTriggerStore = mock(AsyncTriggerStore.class);

    DefaultBrainslugContext createdContext = new BrainslugContextBuilder()
      .withAsyncTriggerScheduler(asyncTriggerScheduler)
      .withTokenStore(tokenStore)
      .withExecutor(flowExecutor)
      .withAsyncTriggerSchedulerOptions(asyncSchedulerOptions)
      .withAsyncFlowStartScheduler(flowStartScheduler)
      .withAsyncTriggerStore(asyncTriggerStore)
      .build();

    assertThat(createdContext.getAsyncTriggerScheduler()).isEqualTo(asyncTriggerScheduler);
    assertThat(createdContext.getTokenStore()).isEqualTo(tokenStore);
    assertThat(createdContext.getFlowExecutor()).isEqualTo(flowExecutor);
    assertThat(createdContext.getAsyncTriggerStore()).isEqualTo(asyncTriggerStore);
  }
}