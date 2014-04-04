package brainslug.flow.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FlowPathDefinitionTest {

  @Test
  public void shouldConnectToExistingEndEvent() {
    // GIVEN:
    FlowDefinition flowDefinition = mock(FlowDefinition.class);
    EventDefinition startEvent = new EventDefinition().id("start");
    EventDefinition endEvent = new EventDefinition().id("end");
    FlowPathDefinition<?> pathDefinition = new FlowPathDefinition(flowDefinition, startEvent);

    when(flowDefinition.contains(any(FlowNodeDefinition.class))).thenReturn(true);
    when(flowDefinition.getNode(endEvent.getId())).thenReturn(endEvent);

    // WHEN:
    pathDefinition.end(endEvent);
    // THEN:
    assertThat(pathDefinition.getPathNodes().getLast().getId().toString()).isEqualTo("end");
  }

}
