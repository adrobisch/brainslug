package brainslug;

import brainslug.flow.FlowBuilder;
import brainslug.flow.Identifier;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.BrainslugContextBuilder;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.SimpleTask;

public class SimpleExamples {

  //# tag::simple[]
  FlowBuilder simpleFlow = new FlowBuilder() {
    @Override
    public void define() {
      start(event(id("start")))
        .execute(task(id("task")).display("A Task"))
        .execute(task(id("task2")).display("Another Task"))
          .end(event(id("end")));
    }
  };
  //# end::simple[]

  static
  //# tag::hello[]
  class HelloWorldFlow extends FlowBuilder {

    public static Identifier helloFlow = id("helloFlow");

    public static Identifier start = id("start");
    public static Identifier helloTask = id("helloTask");

    @Override
    public void define() {
      flowId(helloFlow);

      start(start).execute(task(helloTask, new SimpleTask() {
        @Override
        public void execute(ExecutionContext context) {
          System.out.println("Hello World!");
        }
      }));
    }
  }
  //# end::hello[]

  public void startHello() {
    //# tag::start-hello[]
    //  create brainslug context with defaults
    BrainslugContext context = new BrainslugContextBuilder().build();
    // add the flow definition
    context.addFlowDefinition(new HelloWorldFlow().getDefinition());

    context.startFlow(HelloWorldFlow.helloFlow, HelloWorldFlow.start);
    //# end::start-hello[]
  }

}
