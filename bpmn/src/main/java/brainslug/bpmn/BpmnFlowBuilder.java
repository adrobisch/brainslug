package brainslug.bpmn;

import brainslug.bpmn.task.ServiceTaskDefinition;
import brainslug.bpmn.task.UserTaskDefinition;
import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.ExpressionBuilder;

abstract public class BpmnFlowBuilder extends FlowBuilder {

  public ServiceTaskDefinition serviceTask(Identifier id) {
    return new ServiceTaskDefinition().id(id);
  }

  public UserTaskDefinition userTask(Identifier id) {
    return new UserTaskDefinition().id(id);
  }

  public ExpressionBuilder<JuelExpression> juel(String juelExpression) {
    return new ExpressionBuilder<JuelExpression>(new JuelExpression(juelExpression));
  }

}
