package brainslug.bpmn;

import brainslug.flow.model.*;
import brainslug.flow.model.EventDefinition;
import brainslug.flow.model.expression.Expression;
import brainslug.bpmn.task.ServiceTaskDefinition;
import brainslug.bpmn.task.UserTaskDefinition;
import brainslug.flow.model.marker.IntermediateEvent;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.StartEvent;

import java.util.ArrayList;
import java.util.List;

public class BpmnModelTransformer {

  List<SequenceFlow> sequenceFlows = new ArrayList<SequenceFlow>();

  public BpmnModel toBpmnModel(FlowBuilder flowBuilder) {
    Process process = new Process();
    process.setId(flowBuilder.getDefinition().getId().toString());
    process.setName(flowBuilder.getDefinition().getName());

    BpmnModel model = new BpmnModel();
    model.addProcess(process);

    addNodes(process, flowBuilder);
    addFlows(process);

    return model;
  }

  public String toBpmnXml(FlowBuilder flowBuilder) {
      BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
      try {
        return new String(bpmnXMLConverter.convertToXML(toBpmnModel(flowBuilder)), "UTF-8");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
  }

  private void addFlows(Process process) {
    for (SequenceFlow flow : sequenceFlows) {
      process.addFlowElement(flow);
    }
  }

  private void addNodes(Process process, FlowBuilder flowBuilder) {
    for (FlowNodeDefinition node : flowBuilder.getDefinition().getNodes()) {
      if (node instanceof EventDefinition) {
        addEvent((EventDefinition) node, process);
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
        throw new UnsupportedOperationException("dont know how to transform " + node);
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
    if (then.getPredicateDefinition().getActual() instanceof Expression) {
      Expression expression = (Expression) then.getPredicateDefinition().getActual();
      return expression.getString();
    }
    throw new UnsupportedOperationException("can only get expression string for Expression predicate");
  }

  private SequenceFlow addIncomingSequenceFlowToFirstPathNode(FlowElement flowElement, FlowPathDefinition<?> then) {
    SequenceFlow flow = createSequenceFlow(
      flowElement.getId(),
      then.getPathNodes().get(1).getId().toString()
    );
    sequenceFlows.add(flow);
    return flow;
  }

  private void addMerge(MergeDefinition merge, Process process) {
    ExclusiveGateway gateway = createExclusiveGateway(merge);
    process.addFlowElement(gateway);
  }

  private void addTask(AbstractTaskDefinition task, Process process) {
    if (task instanceof ServiceTaskDefinition) {
      process.addFlowElement(createServiceTask(task));
    }
    else if (task instanceof UserTaskDefinition) {
      process.addFlowElement(createUserTask(task));
    }
    else {
      throw new UnsupportedOperationException("dont know how to transform " + task);
    }
  }

  private void addEvent(EventDefinition event, Process process) {
    if (event.hasMixin(brainslug.flow.model.marker.StartEvent.class)) {
      process.addFlowElement(createStartEvent(event));
    }
    else if (event.hasMixin(brainslug.flow.model.marker.EndEvent.class)) {
      process.addFlowElement(createEndEvent(event));
    }
    else if (event.hasMixin(IntermediateEvent.class)) {
      process.addFlowElement(createIntermediateCatchEvent(event));
    }
    else {
      throw new UnsupportedOperationException("dont know how to transform " + event);
    }
  }

  private FlowElement createIntermediateCatchEvent(EventDefinition event) {
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

  protected StartEvent createStartEvent(EventDefinition event) {
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

  protected EndEvent createEndEvent(EventDefinition event) {
    EndEvent endEvent = new EndEvent();
    endEvent.setId(event.getId().toString());
    endEvent.setName(event.getDisplayName());
    return endEvent;
  }

  protected ServiceTask createServiceTask(AbstractTaskDefinition task) {
    ServiceTask serviceTask = new ServiceTask();
    serviceTask.setId(task.getId().toString());
    serviceTask.setName(task.getDisplayName());
    serviceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
    serviceTask.setImplementation(task.getDelegateClass().getName());
    if (task.isAsync()) {
      serviceTask.setAsynchronous(true);
    }
    return serviceTask;
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
