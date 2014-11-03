# Registry

The brainslug context has a `Registry` where singletons of service classes can be registered:

```java
    context.getRegistry().registerService(Delegate.class, new Delegate());
```

# Service Call

You may use service call definition, to directly invoke a method on a service:

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

# Delegate class:

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
