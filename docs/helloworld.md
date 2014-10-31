## Hello World

A new flow is defined by creating a new `brainslug.model.FlowBuilder`:

```java
  FlowDefinition helloWorldFlow = new FlowBuilder() {
    @Override
    public void define() {
      start(event(id("start"))).execute(task(id("helloTask"), new SimpleTask() {
        @Override
        public void execute(ExecutionContext context) {
          System.out.println("Hello World!");
        }
      }));
    }
  }.getDefinition();
```

which is executed by adding it to a brainslug context and finally starting it:

```java
  //  create brainslug context with defaults
  BrainslugContext context = new BrainslugContext();
  context.addFlowDefinition(helloWorldFlow);

  context.startFlow(helloWorldFlow.getId(), IdUtil.id("start"));
```
