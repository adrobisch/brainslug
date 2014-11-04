# Synopsis

![brainslug](images/brainslug_big.png)

brainslug is a control flow abstraction library.
It allows to model business logic flow of an application as a graph of typed nodes,
which can be transformed to different representations or be executed within a customisable environment.


## Features

* Builder DSL for flow definitions
* [BPMN 2.0 XML](http://www.omg.org/spec/BPMN/2.0/) export using the [Activiti](https://github.com/Activiti/Activiti) Model
* Flow Renderer based on the BPMN symbols
* Quartz Scheduler for Async Tasks


## Example

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

represents the the following flow:

![task_flow](images/task_flow.png)

## Download

The current version is available in the [maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cbrainslug):

```xml
  <dependencies>
  ...
    <dependency>
      <groupId>de.androbit</groupId>
      <artifactId>brainslug-core</artifactId>
      <version>...</version>
    </dependency>
  ...
  </dependencies>
```