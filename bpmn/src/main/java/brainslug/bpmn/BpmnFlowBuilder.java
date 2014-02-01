package brainslug.bpmn;

import brainslug.flow.model.FlowBuilder;
import brainslug.flow.model.Identifier;
import brainslug.bpmn.task.ServiceTaskDefinition;
import brainslug.bpmn.task.UserTaskDefinition;

abstract public class BpmnFlowBuilder extends FlowBuilder {

  public ServiceTaskDefinition serviceTask(Identifier id) {
    return new ServiceTaskDefinition().id(id);
  }

  public UserTaskDefinition userTask(Identifier id) {
    return new UserTaskDefinition().id(id);
  }

}
