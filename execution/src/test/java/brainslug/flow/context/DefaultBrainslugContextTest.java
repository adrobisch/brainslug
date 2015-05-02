package brainslug.flow.context;

import brainslug.flow.execution.FlowExecutor;
import org.junit.Test;

import static brainslug.flow.builder.FlowBuilderSupport.id;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DefaultBrainslugContextTest {

  private FlowExecutor flowExecutor = mock(FlowExecutor.class);

  DefaultBrainslugContext brainslugContext = new BrainslugContextBuilder()
    .withFlowExecutor(flowExecutor)
    .build();

  @Test
  public void signalEventShouldTriggerEventNodeExeution() {
    brainslugContext.signalEvent(id("event"), id("instance"), id("definition"));

    verify(flowExecutor).trigger(new Trigger()
      .nodeId(id("event"))
      .definitionId(id("definition"))
      .instanceId(id("instance"))
      .signaling(true)
    );
  }

}