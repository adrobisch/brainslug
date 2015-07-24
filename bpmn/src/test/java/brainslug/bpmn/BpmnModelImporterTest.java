package brainslug.bpmn;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.node.ChoiceDefinition;
import brainslug.flow.node.EventDefinition;
import brainslug.flow.node.JoinDefinition;
import brainslug.flow.node.TaskDefinition;
import org.junit.Test;

import java.util.Collection;

import static brainslug.util.FlowDefinitionAssert.assertThat;

public class BpmnModelImporterTest {

    @Test
    public void shouldTransformBpmnModelToFlowDefinition() {
        BpmnModelImporter bpmnModelImporter = new BpmnModelImporter();
        Collection<FlowDefinition> flowDefinition = bpmnModelImporter.fromBpmnXml(getClass().getClassLoader().getResourceAsStream("simple-process.bpmn"));

        assertThat(flowDefinition.iterator().next())
                .hasFlowNodesWithType(EventDefinition.class, 3)
                .hasFlowNodesWithType(TaskDefinition.class, 3)
                .hasFlowNodesWithType(ChoiceDefinition.class, 1)
                .hasFlowNodesWithType(JoinDefinition.class, 1)
                .hasEdgesCount(8);

        System.out.println(new BpmnModelExporter().toBpmnXml(flowDefinition.iterator().next()));
    }

}