---
out: index.html
---

# brainslug

brainslug is a control flow abstraction library. 
It allows to model business logic flow of an application as a graph of typed nodes, 
which can be transformed to different representations or be executed within a customisable environment.

## Download

The current version is available in the [maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cbrainslug):

```xml
  <dependencies>
  ...
    <dependency>
      <groupId>de.androbit</groupId>
      <artifactId>brainslug-model</artifactId>
      <version>...</version>
    </dependency>
    <dependency>
      <groupId>de.androbit</groupId>
      <artifactId>brainslug-execution</artifactId>
      <version>...</version>
    </dependency>
  ...
  </dependencies>
```

## Hello World

A new flow is defined by creating a new `brainslug.flow.model.FlowBuilder`:

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

## Another Example

```java
  new FlowBuilder() {
    @Override
    public void define() {
      start(event(id("start")))
        .execute(task(id("task")).display("A Task"))
        .execute(task(id("task2")).display("Another Task"))
      .end(event(id("end")));
    }
  };
```

which represents the the following flow:

![task_flow](images/task_flow.png)


