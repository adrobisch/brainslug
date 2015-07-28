package brainslug.bpmn;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.expression.Expression;
import brainslug.flow.node.ChoiceDefinition;
import brainslug.flow.node.EventDefinition;
import brainslug.flow.node.JoinDefinition;
import brainslug.flow.node.TaskDefinition;
import brainslug.flow.node.task.Task;
import brainslug.util.FlowDefinitionAssert;
import org.junit.Test;

import java.util.Collection;

import static brainslug.util.IdUtil.id;
import static org.assertj.core.api.Assertions.assertThat;

public class BpmnModelImporterTest {

    @Test
    public void shouldTransformBpmnModelToFlowDefinition() {
        BpmnModelImporter bpmnModelImporter = new BpmnModelImporter();
        Collection<FlowDefinition> flowDefinition = bpmnModelImporter.fromBpmnXml(getClass().getClassLoader().getResourceAsStream("simple-process.bpmn"));

        FlowDefinition importedDefinition = flowDefinition.iterator().next();

        FlowDefinitionAssert.assertThat(importedDefinition)
                .hasFlowNodesWithType(EventDefinition.class, 3)
                .hasFlowNodesWithType(TaskDefinition.class, 3)
                .hasFlowNodesWithType(ChoiceDefinition.class, 1)
                .hasFlowNodesWithType(JoinDefinition.class, 1)
                .hasEdgesCount(8);

        TaskDefinition task1 = (TaskDefinition) importedDefinition.getNode(id("Task_0zf0c5m"));

        assertThat(task1.isRetryAsync()).isTrue();

        assertThat(task1.getTaskScript().isPresent()).isTrue();
        assertThat(task1.getTaskScript().get().getLanguage()).isEqualTo("JavaScript");
        assertThat(task1.getTaskScript().get().getText()).isEqualTo("execution.getTrigger().setProperty(\"foo\", \"bar\");");

        TaskDefinition task2 = (TaskDefinition) importedDefinition.getNode(id("Task_09b543m"));

        assertThat(task2.getDelegateClass()).isEqualTo(Task.class);
        assertThat(task2.isAsync()).isTrue();

        assertThat(task2.getConfiguration())
                .containsEntry("param1", "value1")
                .containsEntry("param2", "value2");

        ChoiceDefinition choice = (ChoiceDefinition) importedDefinition.getNode(id("ExclusiveGateway_0s7cjkk"));
        Expression expression = firstChoiceExpression(choice);
        assertThat(expression)
                .isInstanceOf(JuelExpression.class);
        assertThat(((JuelExpression) expression).getValue()).isEqualTo("x == 1");

        System.out.println(new BpmnModelExporter().toBpmnXml(importedDefinition));
    }

    private Expression firstChoiceExpression(ChoiceDefinition choice) {
        return choice.getThenPaths().get(0).getExpression();
    }

}