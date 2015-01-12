package brainslug.example;

import brainslug.flow.FlowBuilder;

public class SimpleFlow extends FlowBuilder {
  @Override
  public void define() {
    flowId(id("simpleFlow"));

    start(id("start")).execute(task(id("task"))).end(id("end"));
  }
}
