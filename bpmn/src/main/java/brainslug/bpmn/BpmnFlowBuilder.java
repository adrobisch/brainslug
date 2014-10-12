package brainslug.bpmn;

import brainslug.flow.FlowBuilder;
import brainslug.flow.Identifier;
import brainslug.bpmn.task.ServiceTaskDefinition;
import brainslug.bpmn.task.UserTaskDefinition;
import brainslug.flow.expression.PredicateBuilder;

abstract public class BpmnFlowBuilder extends FlowBuilder {

  public ServiceTaskDefinition serviceTask(Identifier id) {
    return new ServiceTaskDefinition().id(id);
  }

  public UserTaskDefinition userTask(Identifier id) {
    return new UserTaskDefinition().id(id);
  }

  public PredicateBuilder<JuelExpression> juel(String juelExpression) {
    return new PredicateBuilder<JuelExpression>(new JuelExpression(juelExpression));
  }

}
