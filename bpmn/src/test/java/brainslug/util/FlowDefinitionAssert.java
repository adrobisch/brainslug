package brainslug.util;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.path.FlowEdgeDefinition;
import org.assertj.core.api.ObjectAssert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class FlowDefinitionAssert extends ObjectAssert<FlowDefinition> {
    private final List<FlowNodeDefinition> flowNodes;
    private final Set<FlowEdgeDefinition> flowEdges;

    protected FlowDefinitionAssert(FlowDefinition flowDefinition) {
        super(flowDefinition);

        this.flowNodes = flowDefinition.getNodes();
        this.flowEdges = collectEdges(flowNodes);
    }

    private Set<FlowEdgeDefinition> collectEdges(List<FlowNodeDefinition> flowNodes) {
        Set<FlowEdgeDefinition> edges = new HashSet<FlowEdgeDefinition>();
        for(FlowNodeDefinition nodeDefinition : flowNodes) {
            edges.addAll(nodeDefinition.getIncoming());
        }
        return edges;
    }

    public static FlowDefinitionAssert assertThat(FlowDefinition flowDefinition) {
        return new FlowDefinitionAssert(flowDefinition);
    }

    public FlowDefinitionAssert hasFlowNodesWithType(Class clazz, int expectedCount) {
        int clazzCount = 0;
        for (FlowNodeDefinition node : flowNodes) {
            if (clazz.isInstance(node)) {
                clazzCount++;
            }
        }
        assertEquals("unexpected node count for type " + clazz.getName(), expectedCount, clazzCount);
        return this;
    }

    public FlowDefinitionAssert hasEdgesCount(int expected) {
        assertEquals("unexpected edges count ", expected, flowEdges.size());
        return this;
    }
}
