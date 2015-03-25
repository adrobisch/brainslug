package brainslug.bpmn;

import brainslug.bpmn.task.UserTaskDefinition;
import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.expression.Expression;
import brainslug.flow.node.*;
import brainslug.flow.node.event.AbstractEventDefinition;
import brainslug.flow.node.event.IntermediateEvent;
import brainslug.flow.node.task.AbstractTaskDefinition;
import brainslug.flow.path.AndDefinition;
import brainslug.flow.path.FlowEdgeDefinition;
import brainslug.flow.path.FlowPathDefinition;
import brainslug.flow.path.ThenDefinition;
import brainslug.util.Option;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;

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

  private void addNodes(Process process, FlowBuilder flowBuilder) {
    for (FlowNodeDefinition node : flowBuilder.getDefinition().getNodes()) {
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
    if (then.getPredicateDefinition().getActual() instanceof Expression) {
      Expression expression = (Expression) then.getPredicateDefinition().getActual();
      return expression.getString();
    }
    throw new UnsupportedOperationException("can only get expression string for Expression predicate");
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

    if (Option.of(task.getDelegateClass()).isPresent()) {
      serviceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
      serviceTask.setImplementation(task.getDelegateClass().getName());
    }

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

  protected Task createTask(AbstractTaskDefinition brainslugTask) {
    Task task = new Task();
    task.setId(brainslugTask.getId().toString());
    task.setName(brainslugTask.getDisplayName());
    return task;
  }

}
