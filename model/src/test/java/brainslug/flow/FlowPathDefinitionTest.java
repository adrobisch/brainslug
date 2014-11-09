package brainslug.flow;

import brainslug.flow.node.EventDefinition;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.node.event.AbstractEventDefinition;
import brainslug.flow.path.FlowPathDefinition;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FlowPathDefinitionTest {

  @Test
  public void shouldConnectToExistingEndEvent() {
    // GIVEN:
    FlowDefinition flowDefinition = mock(FlowDefinition.class);
    AbstractEventDefinition startEvent = new EventDefinition().id("start");
    AbstractEventDefinition endEvent = new EventDefinition().id("end");
    FlowPathDefinition<?> pathDefinition = new FlowPathDefinition(flowDefinition, startEvent);

    when(flowDefinition.contains(any(FlowNodeDefinition.class))).thenReturn(true);
    when(flowDefinition.getNode(endEvent.getId())).thenReturn(endEvent);

    // WHEN:
    pathDefinition.end(endEvent);
    // THEN:
    assertThat(pathDefinition.getPathNodes().getLast().getId().toString()).isEqualTo("end");
  }

}
