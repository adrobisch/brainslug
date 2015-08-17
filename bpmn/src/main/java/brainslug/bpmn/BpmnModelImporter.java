package brainslug.bpmn;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.expression.Expression;
import brainslug.flow.node.*;
import brainslug.flow.node.event.*;
import brainslug.flow.path.FlowEdgeDefinition;
import brainslug.flow.path.ThenDefinition;
import brainslug.juel.JuelExpression;
import brainslug.util.Option;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.xml.instance.DomElement;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static brainslug.util.Option.of;
import static brainslug.util.Preconditions.notEmpty;
import static brainslug.util.Preconditions.notNull;
import static brainslug.util.Preconditions.singleItem;

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

        Option<DomElement> brainslugTask = getBrainslugTaskElement(bpmnTask);

        handleBrainslugTaskElement(brainslugTask, bpmnTask, task);

        flowDefinition.addNode(task);
      }

        private void handleBrainslugTaskElement(Option<DomElement> brainslugTask, Task bpmnTask, TaskDefinition taskDefinition) {
            if (!brainslugTask.isPresent()) {
                return;
            }

            Option<String> delegate = brainslugDelegate(brainslugTask.get());
            if (delegate.isPresent()) {
              taskDefinition.delegate(getClassFor(delegate.get()));
            }

            taskDefinition.async(isAsync(brainslugTask.get()));
            taskDefinition.retryAsync(isRetryAsync(brainslugTask.get()));

            addConfigParametersIfPresent(brainslugTask.get(), taskDefinition);
            addScriptIfPresent(brainslugTask.get(), taskDefinition);
        }

        private boolean isRetryAsync(DomElement brainslugTask) {
            String retryAsyncAttribute = "retryAsync";
            return brainslugTask.hasAttribute(retryAsyncAttribute) && Boolean.parseBoolean(brainslugTask.getAttribute(retryAsyncAttribute));
        }

        private void addScriptIfPresent(DomElement brainslugTask, TaskDefinition task) {
          Option<DomElement> scriptElement = uniqueChild(brainslugTask, BrainslugBpmn.BRAINSLUG_BPMN_NS, "script");
          if (scriptElement.isPresent()) {
              Option<String> textContent = of(scriptElement.get().getTextContent());
              if (textContent.isPresent()) {
                  task.script(
                          notEmpty(scriptElement.get().getAttribute("language")),
                          notEmpty(textContent.get().trim())
                  );
              }
          }
      }

      private void addConfigParametersIfPresent(DomElement brainslugTask, TaskDefinition task) {
          Option<DomElement> configElement = uniqueChild(brainslugTask, BrainslugBpmn.BRAINSLUG_BPMN_NS, "configuration");
          if (configElement.isPresent()) {
              Map<String, String> configurationValues = getConfigurationValues(configElement.get());
              task.withConfiguration().parameters(configurationValues);
          }
      }

      private Map<String, String> getConfigurationValues(DomElement configuration) {
        Map<String, String> configValues = new HashMap<String, String>();
        for (DomElement parameter : configuration.getChildElementsByNameNs(BrainslugBpmn.BRAINSLUG_BPMN_NS, "parameter")) {
            configValues.put(notEmpty(parameter.getAttribute("name")), notNull(parameter.getAttribute("value")));
        }
        return configValues;
      }

      private Option<String> brainslugDelegate(DomElement brainslugTask) {
          return of(brainslugTask.getAttribute("delegate"));
      }

        private Boolean isAsync(DomElement brainslugTask) {
            String asyncAttribute = "async";

            return brainslugTask.hasAttribute(asyncAttribute) &&
                    Boolean.parseBoolean(brainslugTask.getAttribute(asyncAttribute));
        }

      private Option<DomElement> getBrainslugTaskElement(Task bpmnTask) {
          if (bpmnTask.getExtensionElements() == null) {
              return Option.empty();
          }

          return uniqueChild(bpmnTask.getExtensionElements().getDomElement(), BrainslugBpmn.BRAINSLUG_BPMN_NS, "task");
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
            choiceDefinition.setOtherwisePath(new ThenDefinition(new JuelExpression("true"), definition, choiceDefinition));
          } else {
            choiceDefinition
              .when(getExpression(sequenceFlow)).getPathNodes().add(target);
          }
        }
      }

      private Expression getExpression(SequenceFlow sequenceFlow) {
        if (sequenceFlow.getConditionExpression() != null) {
          return new JuelExpression(sequenceFlow.getConditionExpression().getTextContent()
                  .replaceFirst("^\\$\\{", "")
                  .replaceFirst("\\}$", "")
          );
        }
        return new JuelExpression("false");
      }
    }

    private Option<DomElement> uniqueChild(DomElement parent, String namespace, String tag) {
        List<DomElement> childElements = parent.getChildElementsByNameNs(namespace, tag);

        if (childElements.isEmpty()) {
            return Option.empty();
        }

        return of(singleItem(childElements));
    }
}
