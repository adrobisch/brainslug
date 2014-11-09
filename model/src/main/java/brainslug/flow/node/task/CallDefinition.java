package brainslug.flow.node.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CallDefinition {
  private List<Object> arguments = new ArrayList<Object>();

  public CallDefinition() {
  }

  public CallDefinition arg(Object argument) {
    arguments.add(argument);
    return this;
  }

  public CallDefinition args(Object[] args) {
    arguments.addAll(Arrays.asList(args));
    return this;
  }

  public List<Object> getArguments() {
    return arguments;
  }
}
