package brainslug;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.context.*;
import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.node.task.SimpleTask;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static brainslug.flow.builder.FlowBuilderSupport.id;

public class BenchmarkExample {

  public static void main(String[] args) {
    final DefaultBrainslugContext brainslugContext = new BrainslugContextBuilder().build();

    final Identifier testFlow = id("testFlow");


    brainslugContext.addFlowDefinition(new FlowBuilder() {

      @Override
      public void define() {
        flowId(testFlow);

        start(task(id("task"), new SimpleTask() {
          @Override
          public void execute(ExecutionContext context) {
            System.out.println(context.getInstance().getIdentifier());
          }
        }));
      }
    }.getDefinition());

    ExecutorService executor = Executors.newFixedThreadPool(10);

    long startTime = System.currentTimeMillis();
    for (int count = 0; count < 10000; count++) {
      executor.submit(new Callable<Object>() {
        @Override
        public Object call() throws Exception {
          return brainslugContext.startFlow(testFlow);
        }
      });
    }

    executor.shutdown();

    try {
      executor.awaitTermination(100, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      Thread.interrupted();
    }

    long endTime = System.currentTimeMillis();

    System.out.println("Execution time: " + (endTime - startTime));
  }

}
