package brainslug.bpmn;

import brainslug.bpmn.task.UserTaskDefinition;
import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.expression.EqualsTrueExpression;
import brainslug.flow.node.*;
import brainslug.flow.node.event.AbstractEventDefinition;
import brainslug.flow.node.event.IntermediateEvent;
import brainslug.flow.node.task.AbstractTaskDefinition;
import brainslug.flow.path.AndDefinition;
import brainslug.flow.path.FlowEdgeDefinition;
import brainslug.flow.path.FlowPathDefinition;
import brainslug.flow.path.ThenDefinition;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class BpmnModelExporter {

  List<SequenceFlow> sequenceFlows = new ArrayList<SequenceFlow>();

  public BpmnModel toBpmnModel(FlowBuilder flowBuilder) {
    return toBpmnModel(flowBuilder.getDefinition());
  }

  public BpmnModel toBpmnModel(FlowDefinition definition) {
    Process process = new Process();
    process.setId(definition.getId().toString());
    process.setName(definition.getName());

    BpmnModel model = new BpmnModel();
    model.addNamespace("brainslug", BrainslugBpmn.BRAINSLUG_BPMN_NS);
    model.addProcess(process);

    addNodes(process, definition);
    addFlows(process);

    return model;
  }

  public String toBpmnXml(FlowBuilder flowBuilder) {
    return toBpmnXml(flowBuilder.getDefinition());
  }

  public String toBpmnXml(FlowDefinition definition) {
      BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
      try {
        return new String(bpmnXMLConverter.convertToXML(toBpmnModel(definition)), "UTF-8");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
  }

  public String toBpmnXml(BpmnModel bpmnModel) {
    BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
    try {
      return new String(bpmnXMLConverter.convertToXML(bpmnModel), "UTF-8");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void addFlows(Process process) {
    for (SequenceFlow flow : sequenceFlows) {
      process.addFlowElement(flow);
    }
  }

  private void addNodes(Process process, FlowDefinition definition) {
    for (FlowNodeDefinition node : definition.getNodes()) {
      if (node instanceof AbstractEventDefinition) {
        addEvent((AbstractEventDefinition) node, process);
        collectOutgoingFlows(node);
      }
      else if (node instanceof AbstractTaskDefinition) {
        addTask((AbstractTaskDefinition) node, process);
        collectOutgoingFlows(node);
      }
      else if (node instanceof ChoiceDefinition) {
        addChoice((ChoiceDefinition) node, process);
      }
      else if (node instanceof ParallelDefinition) {
        addParallel((ParallelDefinition) node, process);
      }
      else if (node instanceof MergeDefinition) {
        addMerge((MergeDefinition) node, process);
        collectOutgoingFlows(node);
      }
      else if (node instanceof JoinDefinition) {
        addJoin((JoinDefinition) node, process);
        collectOutgoingFlows(node);
      }
      else {
        throw new UnsupportedOperationException("unable  to transform " + node);
      }
    }
  }

  private void addJoin(JoinDefinition node, Process process) {
    ParallelGateway gateway = createParallelGateway(node);
    process.addFlowElement(gateway);
  }

  private void addParallel(ParallelDefinition parallel, Process process) {
    ParallelGateway parallelGateway = createParallelGateway(parallel);
    process.addFlowElement(parallelGateway);
    for (AndDefinition andDefinition : parallel.getParallelPaths()) {
      addIncomingSequenceFlowToFirstPathNode(parallelGateway, andDefinition);
    }
  }

  private ParallelGateway createParallelGateway(FlowNodeDefinition parallel) {
    ParallelGateway parallelGateway = new ParallelGateway();
    parallelGateway.setId(parallel.getId().toString());
    return parallelGateway;
  }

  private void addChoice(ChoiceDefinition choice, Process process) {
    ExclusiveGateway gateway = createExclusiveGateway(choice);
    process.addFlowElement(gateway);

    for (ThenDefinition then : choice.getThenPaths()) {
      SequenceFlow flow = addIncomingSequenceFlowToFirstPathNode(gateway, then);
      flow.setConditionExpression(getExpressionString(then));
    }
  }

  private String getExpressionString(ThenDefinition then) {
    if (then.getExpression() instanceof EqualsTrueExpression && ((EqualsTrueExpression) then.getExpression()).getLeft() instanceof JuelExpression) {
      return "${" + ((JuelExpression) ((EqualsTrueExpression) then.getExpression()).getLeft()).getValue() + "}";
    }
    return then.getExpression().toString();
  }

  private SequenceFlow addIncomingSequenceFlowToFirstPathNode(FlowElement flowElement, FlowPathDefinition<?> then) {
    SequenceFlow flow = createSequenceFlow(
            flowElement.getId(),
            then.getPathNodes().getFirst().getId().toString()
    );
    sequenceFlows.add(flow);
    return flow;
  }

  private void addMerge(MergeDefinition merge, Process process) {
    ExclusiveGateway gateway = createExclusiveGateway(merge);
    process.addFlowElement(gateway);
  }

  private void addTask(AbstractTaskDefinition task, Process process) {
    if (task instanceof UserTaskDefinition) {
      process.addFlowElement(createUserTask(task));
    } else {
      process.addFlowElement(createServiceTask(task));
    }
  }

  private void addEvent(AbstractEventDefinition event, Process process) {
    if (event.is(brainslug.flow.node.event.StartEvent.class)) {
      process.addFlowElement(createStartEvent(event));
    }
    else if (event.is(brainslug.flow.node.event.EndEvent.class)) {
      process.addFlowElement(createEndEvent(event));
    }
    else if (event.is(IntermediateEvent.class)) {
      process.addFlowElement(createIntermediateCatchEvent(event));
    }
    else {
      throw new UnsupportedOperationException("unable to transform " + event);
    }
  }

  private FlowElement createIntermediateCatchEvent(AbstractEventDefinition event) {
    IntermediateCatchEvent catchEvent = new IntermediateCatchEvent();
    catchEvent.setId(event.getId().toString());
    catchEvent.setName(event.getDisplayName());
    return catchEvent;
  }

  private void collectOutgoingFlows(FlowNodeDefinition<?> node) {
    for (FlowEdgeDefinition edge : node.getOutgoing()) {
      sequenceFlows.add(createSequenceFlow(edge));
    }
  }

  protected SequenceFlow createSequenceFlow(FlowEdgeDefinition edge) {
    return createSequenceFlow(edge.getSource().getId().toString(), edge.getTarget().getId().toString());
  }

  protected SequenceFlow createSequenceFlow(String source, String target) {
    SequenceFlow flow = new SequenceFlow();
    flow.setSourceRef(source);
    flow.setTargetRef(target);
    return flow;
  }

  protected StartEvent createStartEvent(AbstractEventDefinition event) {
    StartEvent startEvent = new StartEvent();
    startEvent.setId(event.getId().toString());
    startEvent.setName(event.getDisplayName());
    return startEvent;
  }

  protected ExclusiveGateway createExclusiveGateway(FlowNodeDefinition choiceDefinition) {
    ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
    exclusiveGateway.setId(choiceDefinition.getId().toString());
    exclusiveGateway.setName(choiceDefinition.getDisplayName());
    return exclusiveGateway;
  }

  protected EndEvent createEndEvent(AbstractEventDefinition event) {
    EndEvent endEvent = new EndEvent();
    endEvent.setId(event.getId().toString());
    endEvent.setName(event.getDisplayName());
    return endEvent;
  }

  protected ServiceTask createServiceTask(AbstractTaskDefinition task) {
    ServiceTask serviceTask = new ServiceTask();
    serviceTask.setId(task.getId().toString());
    serviceTask.setName(task.getDisplayName());

    addTaskOptions(task, serviceTask);

    if (!task.getConfiguration().isEmpty()) {
      addConfiguration(task, serviceTask);
    }

    if (task.getTaskScript().isPresent()) {
      addScript(task, serviceTask);
    }

    return serviceTask;
  }

  private void addScript(AbstractTaskDefinition<?> task, ServiceTask serviceTask) {
    ExtensionElement scriptElement = new ExtensionElement();
    scriptElement.setName("script");
    scriptElement.setNamespace(BrainslugBpmn.BRAINSLUG_BPMN_NS);

    addAttribute("language", task.getTaskScript().get().getLanguage(), scriptElement);
    scriptElement.setElementText(task.getTaskScript().get().getText());

    serviceTask.addExtensionElement(scriptElement);
  }

  private void addTaskOptions(AbstractTaskDefinition task, ServiceTask serviceTask) {
    ExtensionElement delegateElement = new ExtensionElement();
    delegateElement.setName("task");
    delegateElement.setNamespace(BrainslugBpmn.BRAINSLUG_BPMN_NS);

    if (task.getDelegateClass() != null) {
      addAttribute("delegate", task.getDelegateClass().getName(), delegateElement);
    }

    if (task.isAsync()) {
      addAttribute("async", "" + task.isAsync(), delegateElement);
    }

    if (task.isRetryAsync()) {
      addAttribute("retryAsync", "" + task.isRetryAsync(), delegateElement);
    }

    serviceTask.addExtensionElement(delegateElement);
  }

  private void addAttribute(String name, String value, ExtensionElement delegateElement) {
    ExtensionAttribute attribute = new ExtensionAttribute();
    attribute.setName(name);
    attribute.setValue(value);

    delegateElement.addAttribute(attribute);
  }

  private void addConfiguration(AbstractTaskDefinition<?> task, ServiceTask serviceTask) {
    ExtensionElement configurationElement = new ExtensionElement();
    configurationElement.setName("configuration");
    configurationElement.setNamespace(BrainslugBpmn.BRAINSLUG_BPMN_NS);

    for (Map.Entry<String, String> parameter : task.getConfiguration().entrySet()) {
      ExtensionElement parameterElement = new ExtensionElement();
      parameterElement.setName("parameter");
      parameterElement.setNamespace(BrainslugBpmn.BRAINSLUG_BPMN_NS);

      addAttribute("name", parameter.getKey(), parameterElement);
      addAttribute("value", parameter.getValue(), parameterElement);

      configurationElement.addChildElement(parameterElement);
    }

    serviceTask.addExtensionElement(configurationElement);
  }

  protected UserTask createUserTask(AbstractTaskDefinition task) {
    UserTask userTask = new UserTask();
    userTask.setId(task.getId().toString());
    userTask.setName(task.getDisplayName());
    if (task instanceof UserTaskDefinition) {
      userTask.setAssignee(((UserTaskDefinition) task).getAssignee());
    }
    return userTask;
  }

}
