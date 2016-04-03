package brainslug.example;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.node.task.SimpleTask;

public class TaskExamples {

  public static
  //# tag::example-service[]
  class ExampleService {
    public void doSomething() {
      System.out.println("Done!");
    }
  }
  //# end::example-service[]

  //# tag::inline-task[]
  FlowBuilder inlineTaskFlow = new FlowBuilder() {
    @Override
    public void define() {
      flowId(id("task_flow"));

      start(event(id("start")).display("Start"))
        .execute(task(id("task"), new SimpleTask() {
          @Override
          public void execute(ExecutionContext ctx) {
            ctx.service(ExampleService.class).doSomething();
          }
        }).display("Do Something"))
        .end(event(id("end")).display("End"));
    }
  };
  //# end::inline-task[]

}
