package brainslug.flow.execution.async;

import brainslug.flow.FlowBuilder;
import brainslug.flow.Identifier;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.BrainslugContextBuilder;
import brainslug.flow.context.DefaultBrainslugContext;
import brainslug.util.IdUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.verification.VerificationMode;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

public class DefaultFlowStartSchedulerTest {
  @Test
  public void shouldFindFlowDefinitionsWithStartTimer() {
    DefaultFlowStartScheduler flowStartScheduler = new DefaultFlowStartScheduler();
    BrainslugContext context = getBrainslugContextWithFlows();

    Set<DefaultFlowStartScheduler.TimedFlowDefinition> flowDefinitionsWithStartTimer = flowStartScheduler.getFlowDefinitionsWithStartTimer(context.getDefinitions());
    Assertions.assertThat(flowDefinitionsWithStartTimer)
      .hasSize(1);

    Assertions.assertThat(flowDefinitionsWithStartTimer
      .iterator()
      .next()
      .getFlowDefinition()
      .getId()
      .stringValue()).isEqualTo("withTimer");
  }

  @Test
  public void shouldStartFlows() {
    BrainslugContext context = getBrainslugContextWithFlows();
    DefaultFlowStartScheduler flowStartScheduler = new DefaultFlowStartScheduler();

    flowStartScheduler.start(new SchedulerOptions().withSchedulePeriod(1), context, context.getDefinitions());

    await().until(flowWasStarted(context, IdUtil.id("withTimer"), IdUtil.id("start"), times(2)));
  }

  private Callable<Boolean> flowWasStarted(final BrainslugContext context, final Identifier definitionId, final Identifier startNodeId, final VerificationMode verificationMode) {
    return new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        try {
          verify(context, verificationMode).startFlow(definitionId, startNodeId);
          // verify will throw an assertion error until the mock was called
          return true;
        }catch (AssertionError e) {
          return false;
        }
      }
    };
  }

  private BrainslugContext getBrainslugContextWithFlows() {
    DefaultBrainslugContext object = new BrainslugContextBuilder().build().addFlowDefinition(new FlowBuilder() {

      @Override
      public void define() {
        flowId(id("withTimer"));

        start(event(id("start")), every(1, TimeUnit.SECONDS));
      }
    }.getDefinition()).addFlowDefinition(new FlowBuilder() {

      @Override
      public void define() {
        start(event(id("start")));
      }
    }.getDefinition());

    return spy(object);

  }

}