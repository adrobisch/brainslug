package brainslug.bpmn;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.expression.Expression;
import brainslug.flow.node.*;
import brainslug.flow.node.event.*;
import brainslug.flow.path.FlowEdgeDefinition;
import brainslug.flow.path.ThenDefinition;
import brainslug.util.Option;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BpmnModelImporter {
    public Collection<FlowDefinition> fromBpmnXml(InputStream bpmnStream) {
        BpmnModelInstance modelInstance = Bpmn.readModelFromStream(bpmnStream);
        Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);

        List<FlowDefinition> flowDefinitions = new ArrayList<FlowDefinition>();

        for (Process process: processes) {
            flowDefinitions.add(new BpmnProcessFlowBuilder(process).getDefinition());
        }
        return flowDefinitions;
    }

    class BpmnProcessFlowBuilder extends FlowBuilder {

        final Process bpmnProcess;

        public BpmnProcessFlowBuilder(Process bpmnProcess) {
            this.bpmnProcess = bpmnProcess;
        }

        @Override
        public void define() {
            convertProcessToFlowDefinition();
        }

        void convertProcessToFlowDefinition() {
            List<SequenceFlow> sequenceFlows = new ArrayList<SequenceFlow>();

            for (FlowElement flowElement: bpmnProcess.getFlowElements()) {
                addFlowNode(definition, flowElement, sequenceFlows);
            }

            createEdges(sequenceFlows, definition);
        }

        private FlowDefinition addFlowNode(FlowDefinition flowDefinition, FlowElement flowElement, List<SequenceFlow> sequenceFlows) {
            if (flowElement instanceof StartEvent) {
                start(event(id(flowElement.getId())).display(flowElement.getName()));
            } else if (flowElement instanceof Task) {
              handleTask(flowDefinition, (Task) flowElement);
            } else if (flowElement instanceof IntermediateCatchEvent) {
              flowDefinition.addNode(event(id(flowElement.getId())).display(flowElement.getName()).with(new IntermediateEvent()));
            } else if (flowElement instanceof EndEvent) {
              flowDefinition.addNode(event(id(flowElement.getId())).display(flowElement.getName()).with(new brainslug.flow.node.event.EndEvent()));
            } else if (flowElement instanceof SequenceFlow) {
                sequenceFlows.add((SequenceFlow) flowElement);
            } else if (flowElement instanceof ExclusiveGateway) {
                flowDefinition.addNode(new ChoiceDefinition(flowDefinition).id(flowElement.getId()).display(flowElement.getName()));
            } else if (flowElement instanceof ParallelGateway) {
                // TODO: evaluate flowElement.getGatewayDirection
                if (((ParallelGateway) flowElement).getPreviousNodes().count() < 2) {
                  flowDefinition.addNode(new ParallelDefinition(flowDefinition).id(flowElement.getId()).display(flowElement.getName()));
                } else {
                  flowDefinition.addNode(new JoinDefinition().id(flowElement.getId()).display(flowElement.getName()));
                }
            } else {
                throw new IllegalArgumentException("can't handle flow element: " + flowElement);
            }

            return flowDefinition;
        }

      void handleTask(FlowDefinition flowDefinition, Task bpmnTask) {
        TaskDefinition task = task(id(bpmnTask.getId()))
          .display(bpmnTask.getName());

        Option<String> delegate = brainslugDelegate(bpmnTask);
        if (delegate.isPresent()) {
          task.delegate(getClassFor(delegate.get()));
        }

        flowDefinition.addNode(task);
      }

      private Option<String> brainslugDelegate(Task bpmnTask) {
        return Option.of(bpmnTask.getAttributeValueNs("http://brainslug.it/bpmn-ext", "delegate"));
      }

      private Class<?> getClassFor(String className) {
        try {
          return Class.forName(className);
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }

      void createEdges(List<SequenceFlow> sequenceFlows, FlowDefinition flowDefinition) {
          for (SequenceFlow sequenceFlow : sequenceFlows) {
            FlowNodeDefinition<?> source = flowDefinition.getNode(id(sequenceFlow.getSource().getId()));
            FlowNodeDefinition<?> target = flowDefinition.getNode(id(sequenceFlow.getTarget().getId()));

            handleSequenceFlow(sequenceFlow, source, target);

            addEdge(source, target);
          }
        }

      private void addEdge(FlowNodeDefinition<?> source, FlowNodeDefinition<?> target) {
        FlowEdgeDefinition edge = new FlowEdgeDefinition(source, target);

        source.getOutgoing().add(edge);
        target.getIncoming().add(edge);
      }

      private void handleSequenceFlow(SequenceFlow sequenceFlow, FlowNodeDefinition<?> source, FlowNodeDefinition<?> target) {
        if (source instanceof ChoiceDefinition) {
          ChoiceDefinition choiceDefinition = (ChoiceDefinition) source;
          ExclusiveGateway exclusiveGateway = (ExclusiveGateway) sequenceFlow.getSource();

          if (exclusiveGateway.getDefault() != null && exclusiveGateway.getDefault().getId().equals(sequenceFlow.getId())) {
            choiceDefinition.setOtherwisePath(new ThenDefinition(new JuelExpression("${true}"), definition, choiceDefinition));
          } else {
            choiceDefinition
              .when(getExpression(sequenceFlow)).getPathNodes().add(target);
          }
        }
      }

      private Expression getExpression(SequenceFlow sequenceFlow) {
        if (sequenceFlow.getConditionExpression() != null) {
          return new JuelExpression(sequenceFlow.getConditionExpression().getTextContent());
        }
        return new JuelExpression("${false}");
      }
    }
}
