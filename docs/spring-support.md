# Spring Support

It is possible integrate brainslug with the Spring application context, so that the Spring Beans
are available in the execution context.

## Setup

Just import the `brainslug.spring.SpringBrainslugConfiguration` configuration class into you application context.

Calls to the `ExecutionContext` or the `Registry` will then return the services from the Spring application context.

```java
FlowBuilder flowBuilder = new FlowBuilder() {
  @Override
  public void define() {
    flowId(id("task_flow"));

    start(event(id("start")).display("Start"))
      .execute(task(id("task"), new Task() {
        @Override
        public void execute(ExecutionContext ctx) {
          MyService myService = ctx.service(MyService.class);

          // call to spring bean of type MyService
          myService.doSomething();
        }
      }).display("Do Something"))
    .end(event(id("end")).display("End"));
  }
};
```


