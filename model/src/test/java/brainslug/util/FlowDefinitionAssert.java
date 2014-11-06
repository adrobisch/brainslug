package brainslug.util;

import brainslug.flow.*;
import brainslug.flow.FlowDefinition;
import brainslug.flow.path.FlowEdgeDefinition;
import brainslug.flow.node.FlowNodeDefinition;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class FlowDefinitionAssert extends AbstractAssert<FlowDefinitionAssert, FlowDefinition> {
  protected FlowDefinitionAssert(FlowDefinition actual, Class<?> selfType) {
    super(actual, selfType);
  }

  public static FlowDefinitionAssert assertThat(FlowDefinition flowDefinition) {
    return new FlowDefinitionAssert(flowDefinition, FlowDefinitionAssert.class);
  }

  public FlowDefinitionAssert hasTotalNodes(int expectedCount) {
    Assertions.assertThat(actual.getNodes().size()).isEqualTo(expectedCount);
    return this;
  }

  public FlowDefinitionAssert hasTotalEdges(int expectedCount) {
    int actualEdgeCount = 0;
    for(FlowNodeDefinition<?> node : actual.getNodes()) {
      actualEdgeCount += node.getOutgoing().size();
    }

    Assertions.assertThat(actualEdgeCount).isEqualTo(expectedCount);
    return this;
  }

  public FlowDefinitionAssert hasNodesWithMarker(int expectedCount, Class<?> markerClass) {
    int actualCount = 0;
    for(FlowNodeDefinition node : actual.getNodes()){
      if (node.is(markerClass)) {
        actualCount++;
      }
    }
    Assertions.assertThat(actualCount).isEqualTo(expectedCount);
    return this;
  }

  public FlowDefinitionAssert hasNodesWithType(int expectedCount, Class<?> typeClass) {
    int actualCount = 0;
    for(FlowNodeDefinition node : actual.getNodes()){
      if (typeClass.isInstance(node)) {
        actualCount++;
      }
    }
    Assertions.assertThat(actualCount).isEqualTo(expectedCount);
    return this;
  }

  public FlowDefinitionAssert hasEdge(Identifier source, Identifier target) {
    FlowEdgeDefinition edgeToFind = new FlowEdgeDefinition(actual.getNode(source), actual.getNode(target));
    for(FlowNodeDefinition node : actual.getNodes()){
      if (node.getOutgoing().contains(edgeToFind)) {
        return this;
      }
    }
    throw new AssertionError("edge " + edgeToFind + "not found");
  }

  public FlowDefinitionAssert hasEdge(Enum source, Enum target) {
    return hasEdge(new EnumIdentifier(source), new EnumIdentifier(target));
  }

}
