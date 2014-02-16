package brainslug.bpmn.task;

import brainslug.flow.model.AbstractTaskDefinition;

public class UserTaskDefinition extends AbstractTaskDefinition<UserTaskDefinition> {

  String assignee;

  public UserTaskDefinition assignee(String assignee) {
    this.assignee = assignee;
    return this;
  }

  public String getAssignee() {
    return assignee;
  }

}
