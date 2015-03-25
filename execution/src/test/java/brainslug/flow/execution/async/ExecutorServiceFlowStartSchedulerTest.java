package brainslug.flow.execution.async;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.definition.Identifier;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.BrainslugContextBuilder;
import brainslug.util.IdUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.verification.VerificationMode;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

public class ExecutorServiceFlowStartSchedulerTest {
  @Test
  public void shouldFindFlowDefinitionsWithStartTimer() {
    ExecutorServiceFlowStartScheduler flowStartScheduler = new ExecutorServiceFlowStartScheduler();
    BrainslugContext context = getBrainslugContextWithFlows();

    List<ExecutorServiceFlowStartScheduler.TimedFlowDefinition> flowDefinitionsWithStartTimer = flowStartScheduler.addFlowDefinitionsWithStartTimer(context.getDefinitions());
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
    ExecutorServiceFlowStartScheduler flowStartScheduler = new ExecutorServiceFlowStartScheduler();

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
    BrainslugContext object = new BrainslugContextBuilder().build().addFlowDefinition(new FlowBuilder() {

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