package brainslug;

import brainslug.flow.FlowBuilder;
import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.BrainslugContextBuilder;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.SimpleTask;

import static brainslug.flow.execution.ExecutionProperties.newProperties;

public class SimpleExamples {
  public static
  // # tag::simple[]
  FlowBuilder simpleFlow = new FlowBuilder() {
    @Override
    public void define() {
      flowId(id("simpleFlow"));

      start(event(id("start")))
        .execute(task(id("task")).display("A Task"))
        .execute(task(id("task2")).display("Another Task"))
          .end(event(id("end")));
    }
  };
  // # end::simple[]

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
          System.out.println(String.format(
              "Hello %s!", context.property("name", String.class))
          );
        }
      }));
    }
  }
  //# end::hello[]

  public static void main(String[] args) {
    //# tag::start-hello[]
    //  create brainslug context with defaults
    BrainslugContext context = new BrainslugContextBuilder().build();
    // add the flow definition
    FlowDefinition helloFlow = new HelloWorldFlow().getDefinition();
    context.addFlowDefinition(helloFlow);
    // start the flow with property 'name' = "World"
    context.startFlow(helloFlow, newProperties().with("name", "World"));
    //# end::start-hello[]
  }

}
