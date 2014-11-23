# Registry

The brainslug context has a `Registry` where singletons of service classes can be registered:

```java
context.getRegistry().registerService(Delegate.class, new Delegate());
```

or retrieved:

```java
Delegate delegateService = context.getRegistry().getService(Delegate.class);
```

This services may be accessed by via the `ExecutionContext`:

# Ways to define a task

## Inline Task Definition

```java
FlowBuilder flowBuilder = new FlowBuilder() {
  @Override
  public void define() {
    flowId(id("task_flow"));

    start(event(id("start")).display("Start"))
      .execute(task(id("task"), new Task() {
        @Override
        public void execute(ExecutionContext ctx) {
          ctx.service(MyService.class).doSomething();
        }
      }).display("Do Something"))
    .end(event(id("end")).display("End"));
  }
};
```

Starting with **Java 8** this might be done using a lambda expression:

```java
FlowBuilder flowBuilder = new FlowBuilder() {
  @Override
  public void define() {
    flowId(id("task_flow"));

    start(event(id("start")).display("Start"))
      .execute(task(id("task"), ctx -> {
          ctx.service(MyService.class).doSomething();
      }).display("Do Something"))
    .end(event(id("end")).display("End"));
  }
};
```

## Delegate class:

If you do not want to specify the method by name, you can use the `Execute`-annotation to define which
method you want be executed for a task:

```java
class Delegate {
  @Execute
  public void execute(ExecutionContext context) {
  ...
  }
}
new FlowBuilder() {
  @Override
  public void define() {
    start(event(id(START)))
      .execute(task(id(TASK)).delegate(Delegate.class)))
    .end(event(id(END)));
  }
}
```

## Typesafe Service Call Definition

It possible to define service calls using a proxy-based approach similar to Mockito.

```java
public interface MyService {
  public String echo(String input);
}
...

FlowBuilder flowBuilder = new FlowBuilder() {
  @Override
  public void define() {
    flowId(id("task_flow"));

    MyService service = service(MyService.class);

    start(event(id("start")).display("Start"))
      .execute(task(id("task")).call(method(
        service.echo(val(constant("value to be echoed")))
      )).display("Do Something"))
    .end(event(id("end")).display("End"));
  }
};
```

In this case, the call to the service will be made at execution using the recorded argument values.
This will be done using reflection on the instance of the service, which must be available in the `Registry`.

## Service Call

You may use service call definition, to directly define the invocation of a method during flow node definition.

```java
new FlowBuilder() {
  @Override
  public void define() {
    start(event(id(START)))
    .execute(task(id(TASK))
      .call(service(TestService.class).method("getString")))
    .end(event(id(END)));
  }
}
```
